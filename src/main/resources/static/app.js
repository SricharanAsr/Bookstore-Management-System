// Auth State
let isLoginMode = true;
let currentUser = null;

// Modal Logic
const authModal = document.getElementById('authModal');
const authBtn = document.getElementById('authBtn');
const closeModal = document.getElementById('closeModal');
const toggleAuthMode = document.getElementById('toggleAuthMode');
const authForm = document.getElementById('authForm');
const modalTitle = document.getElementById('modalTitle');
const nameField = document.getElementById('nameField');
const submitAuth = document.getElementById('submitAuth');

authBtn.onclick = () => {
    if (currentUser) {
        logout();
    } else {
        authModal.style.display = 'flex';
    }
}

closeModal.onclick = () => {
    authModal.style.display = 'none';
}

window.onclick = (event) => {
    if (event.target == authModal) {
        authModal.style.display = 'none';
    }
}

toggleAuthMode.onclick = (e) => {
    e.preventDefault();
    isLoginMode = !isLoginMode;
    modalTitle.innerText = isLoginMode ? 'Login' : 'Create Account';
    nameField.classList.toggle('hidden', isLoginMode);
    submitAuth.innerText = isLoginMode ? 'Login' : 'Register';
    toggleAuthMode.innerText = isLoginMode ? 'Create one' : 'Login instead';
}

authForm.onsubmit = async (e) => {
    e.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const name = document.getElementById('name').value;

    const endpoint = isLoginMode ? '/api/login' : '/api/register';
    const body = isLoginMode ? { email, password } : { name, email, password };

    try {
        const response = await fetch(endpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });

        if (response.ok) {
            const data = await response.json();
            if (isLoginMode) {
                localStorage.setItem('token', data.token);
                localStorage.setItem('userEmail', email);
                currentUser = email;
                updateAuthUI();
                authModal.style.display = 'none';
                alert('Login successful!');
            } else {
                alert('Registration successful! Please login.');
                isLoginMode = true;
                toggleAuthMode.click();
            }
        } else {
            const error = await response.json();
            alert('Error: ' + (error.message || 'Authentication failed'));
        }
    } catch (err) {
        console.error('Auth error:', err);
        alert('An error occurred. Make sure the backend is running.');
    }
}

// App State
let allBooks = [];

async function fetchBooks() {
    try {
        const response = await fetch('/api/books');
        const data = await response.json();
        allBooks = data.content; // Cache books
        renderBooks(allBooks);
        if (currentUser && localStorage.getItem('userEmail') === 'admin@skybrisk.com') {
            const totalBooksEl = document.getElementById('totalBooks');
            if (totalBooksEl) totalBooksEl.innerText = `Total Books: ${data.totalElements}`;
        }
    } catch (error) {
        console.error('Error fetching books:', error);
        document.getElementById('booksGrid').innerHTML = '<p>Connect to the backend to view our collection.</p>';
    }
}

function renderBooks(books) {
    const grid = document.getElementById('booksGrid');
    if (!books || books.length === 0) {
        grid.innerHTML = '<p>No books found.</p>';
        return;
    }

    grid.innerHTML = books.map(book => `
        <div class="book-card" onclick="showBookDetails(${book.id})">
            <img src="${book.imageUrl || 'https://via.placeholder.com/300x450?text=No+Cover'}" alt="${book.title}" class="book-img">
            <div class="book-info">
                <h3>${book.title}</h3>
                <p>by ${book.author}</p>
                <div class="price-tag">$${book.price}</div>
            </div>
        </div>
    `).join('');
}

const detailModal = document.getElementById('bookDetailsModal');
const closeDetailModal = document.getElementById('closeDetailModal');

if (closeDetailModal) {
    closeDetailModal.onclick = () => {
        detailModal.style.display = 'none';
    }
}

function showBookDetails(bookId) {
    const book = allBooks.find(b => b.id === bookId);
    if (!book) return;

    document.getElementById('detailImg').src = book.imageUrl || 'https://via.placeholder.com/300x450?text=No+Cover';
    document.getElementById('detailTitle').innerText = book.title;
    document.getElementById('detailAuthor').innerText = book.author;
    document.getElementById('detailGenre').innerText = book.genre || 'N/A';
    document.getElementById('detailIsbn').innerText = `ISBN: ${book.isbn || 'N/A'}`;
    document.getElementById('detailPrice').innerText = `$${book.price}`;
    document.getElementById('detailStock').innerText = `Stock: ${book.stockQuantity}`;
    document.getElementById('detailDescription').innerText = book.description || 'No description available for this book.';

    const actionDiv = document.getElementById('detailAction');
    if (currentUser) {
        actionDiv.innerHTML = `<button class="cta-primary full-width" onclick="placeOrder(${book.id}); detailModal.style.display='none';">Order Now</button>`;
    } else {
        actionDiv.innerHTML = `<p class="toggle-auth">Please <a href="#" onclick="detailModal.style.display='none'; authBtn.click(); return false;">Login</a> to place an order.</p>`;
    }

    detailModal.style.display = 'flex';
}

window.onclick = (event) => {
    if (event.target.classList.contains('modal')) {
        event.target.style.display = 'none';
        // Reset scroll when closing book details
        if (event.target.id === 'bookDetailsModal') {
            const info = document.querySelector('.book-detail-info');
            if (info) info.scrollTop = 0;
        }
    }
}

async function placeOrder(bookId) {
    const token = localStorage.getItem('token');
    if (!token) {
        alert('Please login to place an order.');
        authBtn.click();
        return;
    }

    const orderRequest = {
        items: [{ bookId: bookId, quantity: 1 }]
    };

    try {
        const response = await fetch('/api/orders', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(orderRequest)
        });

        if (response.ok) {
            alert('Order placed successfully!');
            fetchOrders();
        } else {
            const error = await response.json();
            alert('Order failed: ' + (error.message || 'Unknown error'));
        }
    } catch (err) {
        console.error('Order error:', err);
    }
}

async function fetchOrders() {
    const token = localStorage.getItem('token');
    if (!token) return;

    const email = localStorage.getItem('userEmail');
    const endpoint = email === 'admin@skybrisk.com' ? '/api/orders' : '/api/orders/my';

    try {
        const response = await fetch(endpoint, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.ok) {
            const orders = await response.json();
            renderOrders(orders);
        }
    } catch (err) {
        console.error('Fetch orders error:', err);
    }
}

function renderOrders(orders) {
    const container = document.getElementById('ordersList');
    if (!orders || orders.length === 0) {
        container.innerHTML = '<p>You haven\'t placed any orders yet.</p>';
        return;
    }

    const userEmail = localStorage.getItem('userEmail');
    const isAdmin = userEmail === 'admin@skybrisk.com';
    console.log('Rendering orders. Admin mode:', isAdmin);

    container.innerHTML = orders.map(order => `
        <div class="order-card">
            <div class="order-info">
                <h4>Order #${order.id}</h4>
                <p>Date: ${new Date(order.orderDate).toLocaleDateString()}</p>
                <p>Total: $${order.totalPrice}</p>
                <p>Status: <span class="status-text status-${order.status.toLowerCase()}">${order.status}</span></p>
                ${isAdmin ? `
                    <div class="admin-actions">
                        ${order.status === 'PENDING' ? `<button class="cta-status dispatch" onclick="updateStatus(${order.id}, 'SHIPPED')">Dispatch Order</button>` : ''}
                        ${order.status === 'SHIPPED' ? `<button class="cta-status deliver" onclick="updateStatus(${order.id}, 'DELIVERED')">Deliver Order</button>` : ''}
                        ${order.status === 'DELIVERED' ? '<span class="delivered-badge">✅ Completed</span>' : ''}
                    </div>
                ` : ''}
            </div>
            <div class="status-badge status-${order.status.toLowerCase()}">${order.status}</div>
        </div>
    `).join('');
}

async function updateStatus(orderId, newStatus) {
    const token = localStorage.getItem('token');
    if (!confirm(`Are you sure you want to mark Order #${orderId} as ${newStatus}?`)) return;

    try {
        const response = await fetch(`/api/orders/${orderId}/status?status=${newStatus}`, {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.ok) {
            console.log('Status updated successfully');
            alert(`SUCCESS: Order #${orderId} is now ${newStatus}`);
            fetchOrders();
        } else {
            const err = await response.text();
            alert('Error: ' + err);
        }
    } catch (err) {
        console.error('Update status error:', err);
        alert('Network error. Check console.');
    }
}

function updateAuthUI() {
    const token = localStorage.getItem('token');
    const landingPage = document.getElementById('landingPage');
    const mainApp = document.getElementById('mainApp');
    const hubLink = document.getElementById('hubLink');
    const booksLink = document.getElementById('booksLink');
    const ordersLink = document.getElementById('ordersLink');
    const adminLink = document.getElementById('adminLink');
    const adminHubCard = document.getElementById('adminHubCard');

    if (token) {
        authBtn.innerText = 'Logout';
        currentUser = localStorage.getItem('userEmail');

        // Show main app and hide landing
        landingPage.classList.add('hidden');
        mainApp.classList.remove('hidden');

        // Default to Home Hub view
        switchView('homeHub');

        hubLink.classList.remove('hidden');
        booksLink.classList.remove('hidden');
        ordersLink.classList.remove('hidden');

        if (document.querySelector('.nav-guest')) {
            document.querySelector('.nav-guest').classList.add('hidden');
        }

        // Fetch data
        fetchOrders();
        fetchBooks();

        // Admin Features
        if (currentUser === 'admin@skybrisk.com') {
            adminLink.classList.remove('hidden');
            if (adminHubCard) adminHubCard.classList.remove('hidden');
        } else {
            adminLink.classList.add('hidden');
            if (adminHubCard) adminHubCard.classList.add('hidden');
        }
    } else {
        authBtn.innerText = 'Login';
        currentUser = null;

        // Show Landing, Hide App
        landingPage.classList.remove('hidden');
        mainApp.classList.add('hidden');

        hubLink.classList.add('hidden');
        booksLink.classList.add('hidden');
        ordersLink.classList.add('hidden');
        adminLink.classList.add('hidden');
    }
}

function switchView(viewId) {
    console.log('Switching to view:', viewId);

    // Hide all views and remove active status
    const views = document.querySelectorAll('.view');
    views.forEach(v => {
        v.classList.add('hidden');
        v.classList.remove('active');
    });

    // Show target view
    const targetView = document.getElementById(viewId);
    if (targetView) {
        // Ensure mainApp is visible if a view is being shown
        document.getElementById('mainApp').classList.remove('hidden');
        document.getElementById('landingPage').classList.add('hidden');

        targetView.classList.remove('hidden');
        // Small delay to trigger animation
        setTimeout(() => {
            targetView.classList.add('active');
        }, 50);
    }

    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userEmail');
    currentUser = null;
    alert('Logged out successfully.');
    window.location.reload(); // Refresh to ensure clean state
}

async function searchBooks() {
    const query = document.getElementById('searchInput').value;
    try {
        const response = await fetch(`/api/books?search=${query}`);
        const data = await response.json();
        renderBooks(data.content);
    } catch (error) {
        console.error('Error searching books:', error);
    }
}

const addBookModal = document.getElementById('addBookModal');
const closeAddBook = document.getElementById('closeAddBook');
const addBookForm = document.getElementById('addBookForm');

function showAddBookModal() {
    addBookModal.style.display = 'flex';
}

if (closeAddBook) {
    closeAddBook.onclick = () => {
        addBookModal.style.display = 'none';
    }
}

if (addBookForm) {
    addBookForm.onsubmit = async (e) => {
        e.preventDefault();
        const token = localStorage.getItem('token');

        const bookData = {
            title: document.getElementById('bookTitle').value,
            author: document.getElementById('bookAuthor').value,
            genre: document.getElementById('bookGenre').value,
            price: parseFloat(document.getElementById('bookPrice').value),
            stockQuantity: parseInt(document.getElementById('bookStock').value),
            imageUrl: document.getElementById('bookImg').value
        };

        try {
            const response = await fetch('/api/books', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(bookData)
            });

            if (response.ok) {
                alert('Book added successfully!');
                addBookModal.style.display = 'none';
                fetchBooks();
            } else {
                alert('Failed to add book. Ensure you have Admin privileges.');
            }
        } catch (err) {
            console.error('Add book error:', err);
        }
    }
}

async function viewAllOrders() {
    const token = localStorage.getItem('token');
    try {
        const response = await fetch('/api/orders', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.ok) {
            const orders = await response.json();
            alert(`Found ${orders.length} total orders across all users.`);
            renderOrders(orders);
        }
    } catch (err) {
        console.error('Fetch all orders error:', err);
    }
}

// Initial fetch & UI check
document.addEventListener('DOMContentLoaded', () => {
    // Theme Init
    const savedTheme = localStorage.getItem('theme') || 'theme-dark';
    document.body.className = savedTheme;

    // DO NOT fetch books here if not logged in to respect the gate
    updateAuthUI();
});

function toggleTheme() {
    const isDark = document.body.classList.contains('theme-dark');
    const newTheme = isDark ? 'theme-light' : 'theme-dark';
    document.body.className = newTheme;
    localStorage.setItem('theme', newTheme);

    // Update toggle icon
    const icon = document.querySelector('#themeToggle .icon');
    if (icon) icon.innerText = isDark ? '🌞' : '🌓';
}

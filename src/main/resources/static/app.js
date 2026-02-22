// Shared Auth State
let currentUser = localStorage.getItem('userEmail');
const token = localStorage.getItem('token');

// Page Detection
const currentPage = window.location.pathname.split('/').pop() || 'index.html';

// Global UI Elements (if they exist on the current page)
const authBtn = document.getElementById('authBtn');
const hubLink = document.getElementById('hubLink');
const adminLink = document.getElementById('adminLink');

function updateAuthUI() {
    if (token) {
        if (authBtn) {
            authBtn.innerText = 'Logout';
            authBtn.href = '#';
            authBtn.onclick = (e) => {
                e.preventDefault();
                logout();
            };
        }
        if (hubLink) hubLink.classList.remove('hidden');
        if (adminLink && currentUser === 'admin@skybrisk.com') {
            adminLink.classList.remove('hidden');
        }
    } else {
        if (authBtn && currentPage !== 'login.html') {
            authBtn.innerText = 'Login';
            authBtn.href = 'login.html';
        }
    }
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userEmail');
    alert('Logged out successfully.');
    window.location.href = 'index.html';
}

function toggleTheme() {
    const isDark = document.body.classList.contains('theme-dark');
    const newTheme = isDark ? 'theme-light' : 'theme-dark';
    document.body.className = newTheme;
    localStorage.setItem('theme', newTheme);
    const icon = document.querySelector('#themeToggle .icon');
    if (icon) icon.innerText = isDark ? '🌞' : '🌓';
}

// Authentication Logic (login.html)
if (currentPage === 'login.html') {
    const authForm = document.getElementById('authForm');
    const toggleAuthMode = document.getElementById('toggleAuthMode');
    const modalTitle = document.getElementById('modalTitle');
    const nameField = document.getElementById('nameField');
    const submitAuth = document.getElementById('submitAuth');
    let isLoginMode = true;

    if (toggleAuthMode) {
        toggleAuthMode.onclick = (e) => {
            e.preventDefault();
            isLoginMode = !isLoginMode;
            modalTitle.innerText = isLoginMode ? 'Login' : 'Create Account';
            nameField.classList.toggle('hidden', isLoginMode);
            submitAuth.innerText = isLoginMode ? 'Login' : 'Register';
            toggleAuthMode.innerText = isLoginMode ? 'Create one' : 'Login instead';
        };
    }

    if (authForm) {
        authForm.onsubmit = async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const name = document.getElementById('name') ? document.getElementById('name').value : '';

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
                        alert('Login successful!');
                        window.location.href = 'hub.html';
                    } else {
                        alert('Registration successful! Please login.');
                        isLoginMode = true;
                        if (toggleAuthMode) toggleAuthMode.click();
                    }
                } else {
                    const error = await response.json();
                    alert('Error: ' + (error.message || 'Authentication failed'));
                }
            } catch (err) {
                console.error('Auth error:', err);
                alert('An error occurred. Make sure the backend is running.');
            }
        };
    }
}

// Global App Functions
let allBooks = [];

async function fetchBooks() {
    try {
        const response = await fetch('/api/books');
        const data = await response.json();
        allBooks = data.content;
        renderBooks(allBooks);
        if (currentPage === 'admin.html') {
            const totalBooksEl = document.getElementById('totalBooks');
            if (totalBooksEl) totalBooksEl.innerText = `Total Books: ${data.totalElements}`;
        }
    } catch (error) {
        console.error('Error fetching books:', error);
        const grid = document.getElementById('booksGrid');
        if (grid) grid.innerHTML = '<p>Error loading books. Check connection.</p>';
    }
}

function renderBooks(books) {
    const grid = document.getElementById('booksGrid');
    if (!grid) return;
    if (!books || books.length === 0) {
        grid.innerHTML = '<p>No books found.</p>';
        return;
    }

    grid.innerHTML = books.map(book => `
        <div class="book-card" onclick="window.location.href='book-details.html?id=${book.id}'">
            <img src="${book.imageUrl || 'https://via.placeholder.com/300x450?text=No+Cover'}" alt="${book.title}" class="book-img">
            <div class="book-info">
                <h3>${book.title}</h3>
                <p>by ${book.author}</p>
                <div class="price-tag">$${book.price}</div>
            </div>
        </div>
    `).join('');
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

// Book Details (book-details.html)
if (currentPage === 'book-details.html') {
    const urlParams = new URLSearchParams(window.location.search);
    const bookId = urlParams.get('id');

    async function loadBookDetails() {
        if (!bookId) return;
        try {
            const response = await fetch(`/api/books/${bookId}`);
            if (response.ok) {
                const book = await response.json();
                document.getElementById('detailImg').src = book.imageUrl || 'https://via.placeholder.com/300x450?text=No+Cover';
                document.getElementById('detailTitle').innerText = book.title;
                document.getElementById('detailAuthor').innerText = book.author;
                document.getElementById('detailGenre').innerText = book.genre || 'N/A';
                document.getElementById('detailIsbn').innerText = `ISBN: ${book.isbn || 'N/A'}`;
                document.getElementById('detailPrice').innerText = `$${book.price}`;
                document.getElementById('detailStock').innerText = `Stock: ${book.stockQuantity}`;
                document.getElementById('detailDescription').innerText = book.description || 'No description available for this book.';

                const actionDiv = document.getElementById('detailAction');
                if (token) {
                    actionDiv.innerHTML = `<button class="cta-primary full-width" onclick="placeOrder(${book.id})">Order Now</button>`;
                } else {
                    actionDiv.innerHTML = `<p class="toggle-auth">Please <a href="login.html">Login</a> to place an order.</p>`;
                }
            }
        } catch (err) {
            console.error('Error loading book details:', err);
        }
    }
    loadBookDetails();
}

async function placeOrder(bookId) {
    if (!token) {
        alert('Please login to place an order.');
        window.location.href = 'login.html';
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
            window.location.href = 'orders.html';
        } else {
            const error = await response.json();
            alert('Order failed: ' + (error.message || 'Unknown error'));
        }
    } catch (err) {
        console.error('Order error:', err);
    }
}

// Orders Logic
async function fetchOrders() {
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
    if (!container) return;
    if (!orders || orders.length === 0) {
        container.innerHTML = '<p>No orders found.</p>';
        return;
    }

    const isAdmin = currentUser === 'admin@skybrisk.com';
    container.innerHTML = orders.map(order => `
        <div class="order-card">
            <div class="order-info">
                <h4>Order #${order.id}</h4>
                <p>Date: ${new Date(order.orderDate).toLocaleDateString()}</p>
                <p>Total: $${order.totalPrice}</p>
                <p>Status: <span class="status-text status-${order.status.toLowerCase()}">${order.status}</span></p>
                ${isAdmin ? `
                    <div class="admin-actions" style="margin-top: 1rem;">
                        ${order.status === 'PENDING' ? `<button class="cta-status dispatch" onclick="updateStatus(${order.id}, 'SHIPPED')">Dispatch</button>` : ''}
                        ${order.status === 'SHIPPED' ? `<button class="cta-status deliver" onclick="updateStatus(${order.id}, 'DELIVERED')">Deliver</button>` : ''}
                    </div>
                ` : ''}
            </div>
            <div class="status-badge status-${order.status.toLowerCase()}">${order.status}</div>
        </div>
    `).join('');
}

async function updateStatus(orderId, newStatus) {
    if (!confirm(`Mark Order #${orderId} as ${newStatus}?`)) return;
    try {
        const response = await fetch(`/api/orders/${orderId}/status?status=${newStatus}`, {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.ok) {
            alert(`SUCCESS: Order #${orderId} is now ${newStatus}`);
            fetchOrders();
        }
    } catch (err) {
        console.error('Update status error:', err);
    }
}

// Admin Logic (admin.html)
if (currentPage === 'admin.html') {
    const addBookModal = document.getElementById('addBookModal');
    const closeAddBook = document.getElementById('closeAddBook');
    const addBookForm = document.getElementById('addBookForm');
    let uploadedImageBase64 = '';

    window.showAddBookModal = () => { if (addBookModal) addBookModal.style.display = 'flex'; };
    if (closeAddBook) closeAddBook.onclick = () => { addBookModal.style.display = 'none'; };

    // Image preview handler
    const bookImgInput = document.getElementById('bookImg');
    if (bookImgInput) {
        bookImgInput.addEventListener('change', (e) => {
            const file = e.target.files[0];
            const previewDiv = document.getElementById('imgPreview');
            const previewImg = document.getElementById('imgPreviewImg');
            if (file) {
                const reader = new FileReader();
                reader.onload = (ev) => {
                    uploadedImageBase64 = ev.target.result; // data:image/...;base64,...
                    if (previewImg) previewImg.src = uploadedImageBase64;
                    if (previewDiv) previewDiv.style.display = 'block';
                };
                reader.readAsDataURL(file);
            } else {
                uploadedImageBase64 = '';
                if (previewDiv) previewDiv.style.display = 'none';
            }
        });
    }

    if (addBookForm) {
        addBookForm.onsubmit = async (e) => {
            e.preventDefault();
            const bookData = {
                title: document.getElementById('bookTitle').value,
                author: document.getElementById('bookAuthor').value,
                genre: document.getElementById('bookGenre').value,
                price: parseFloat(document.getElementById('bookPrice').value),
                stockQuantity: parseInt(document.getElementById('bookStock').value),
                imageUrl: uploadedImageBase64 || ''
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
                    uploadedImageBase64 = '';
                    addBookForm.reset();
                    const previewDiv = document.getElementById('imgPreview');
                    if (previewDiv) previewDiv.style.display = 'none';
                    fetchBooks();
                } else {
                    alert('Failed to add book. Admin access required.');
                }
            } catch (err) {
                console.error('Add book error:', err);
            }
        };
    }
}

window.viewAllOrders = async () => {
    fetchOrders(); // This handles both admin and user based on token
};

// Initial Page Load
document.addEventListener('DOMContentLoaded', () => {
    // Theme Init
    const savedTheme = localStorage.getItem('theme') || 'theme-dark';
    document.body.className = savedTheme;
    const icon = document.querySelector('#themeToggle .icon');
    if (icon) icon.innerText = savedTheme === 'theme-dark' ? '🌓' : '🌞';

    updateAuthUI();

    // Data Loading based on page
    if (currentPage === 'books.html') fetchBooks();
    if (currentPage === 'orders.html') fetchOrders();
    if (currentPage === 'admin.html') {
        fetchBooks();
        fetchOrders();
    }
    if (currentPage === 'hub.html') {
        if (!token) window.location.href = 'login.html';
        const adminHubCard = document.getElementById('adminHubCard');
        if (adminHubCard && currentUser === 'admin@skybrisk.com') adminHubCard.classList.remove('hidden');
    }
});

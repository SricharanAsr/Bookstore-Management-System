# Bookstore Management System REST API

A Spring Boot-based backend system for managing books, users, and orders with JWT authentication and role-based access control.

## Project Objective
The Skybrisk Bookstore Management System aims to provide a premium, "Booky" user experience. It blends modern RESTful API standards with a unique frontend aesthetic inspired by parchment textures, leather bindings, and literary typography.

## Features

- **User Authentication**: Register and login with secure password hashing (BCrypt) and JWT tokens.
- **Role-Based Access Control**: Separate permissions for `ADMIN` and `CUSTOMER` roles.
- **Book Management**: Full CRUD operations for books, search by title/author, and pagination.
- **Image Upload**: Admins can upload book cover images (JPEG, JPG, PNG) directly from the dashboard.
- **Order Management**: Customers can place orders (stocks are automatically updated), and admins can manage order statuses.
- **Multi-Page Frontend**: Premium "Booky" UI with dedicated pages for login, library hub, book browsing, orders, admin dashboard, and book details.
- **Database Integration**: MySQL support for persistent data storage.
- **API Documentation**: Interactive Swagger/OpenAPI documentation.
- **Error Handling**: Centralized global exception handling with meaningful error messages.

## Frontend Pages

| Page | URL | Description |
|------|-----|-------------|
| Landing | `/index.html` | Premium landing page with hero section |
| Login | `/login.html` | Authentication portal (login/register) |
| Hub | `/hub.html` | Main library hub for logged-in users |
| Books | `/books.html` | Full-page collection browser with search |
| Orders | `/orders.html` | Order history and tracking |
| Admin | `/admin.html` | Inventory and order management dashboard |
| Book Details | `/book-details.html?id=N` | Immersive single-book view |

## Prerequisites

- Java 17 or higher
- MySQL Database
- Maven (if not using the wrapper)

## Getting Started

1. **Database Setup**:
   - Create a MySQL database named `bookstore_db`.
   - Update `src/main/resources/application.properties` with your MySQL credentials.

2. **Build and Run**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. **API Documentation**:
   Once the application is running, access Swagger UI at:
   `http://localhost:8080/swagger-ui.html`

## API Endpoints

### Authentication
- `POST /api/register`: Register a new user.
- `POST /api/login`: Authenticate and get JWT token.

### Books
- `GET /api/books`: List all books (Public).
- `GET /api/books/{id}`: Get book details (Public).
- `POST /api/books`: Add a new book (Admin Only).
- `PUT /api/books/{id}`: Update book (Admin Only).
- `DELETE /api/books/{id}`: Delete book (Admin Only).

### Orders
- `POST /api/orders`: Place a new order (Customer Only).
- `GET /api/orders`: List all orders (Admin Only).
- `GET /api/orders/{id}`: Get order details.
- `PUT /api/orders/{id}/status`: Update order status (Admin Only).

## Contributors

- **Sricharan Asr** - Lead Developer & UI Architecture
- **Skybrisk Team** - Project Oversight

## License

This project is licensed under the MIT License - see the LICENSE file for details.

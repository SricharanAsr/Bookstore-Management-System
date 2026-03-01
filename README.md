# Skybrisk Bookstore Management System

A full-stack Bookstore Management System built with **Spring Boot** (backend) and a premium, multi-page **HTML/CSS/JS** frontend. The application features JWT-based authentication, role-based access control (Admin & Customer), a complete book inventory system with image uploads, and a sophisticated order management pipeline — all wrapped in a stunning literary-inspired "Booky" UI.

## Project Summary

The Skybrisk Bookstore Management System is a complete web application designed for managing a bookstore's end-to-end operations. The backend is a robust **Spring Boot REST API** with MySQL persistence, secured using **JWT authentication** and **Spring Security** with role-based access (`ADMIN` / `CUSTOMER`). Admins can manage the book inventory (add, update, delete books with cover image uploads) and process customer orders through a status pipeline (Pending → Shipped → Delivered). Customers can browse the full book collection, view detailed book pages, and place orders.

The frontend is a **Multi-Page Application (MPA)** with 7 dedicated pages, styled with a premium "Booky" aesthetic featuring antique gold and bronze accents, leather and parchment textures, and refined literary typography using Playfair Display and Merriweather fonts. Each page is fully responsive with glassmorphic navigation, animated hover effects, and a dark/light theme toggle.

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

3. **Access the Project**:
   Once the application is running, you can access the system at:
   - **Frontend**: [http://localhost:8080/index.html](http://localhost:8080/index.html)
   - **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - **H2 Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:bookstore_db`)

4. **API Documentation**:
   Once the application is running, access Swagger UI at:
   `http://localhost:8080/swagger-ui.html`

## Default Credentials

### Admin Access
- **Email**: `admin@skybrisk.com`
- **Password**: `adminpassword`

### Customer Access
- Use the **Register** button on the Login page to create a new customer account.

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

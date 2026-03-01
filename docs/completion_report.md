# Project Completion Report: Bookstore Management System REST API

## 1. Project Overview
The Bookstore Management System is a comprehensive RESTful API built using **Java (Spring Boot)**. It provides a secure and efficient way to manage a bookstore's inventory, user authentication, and order processing. A premium, literary-inspired "Booky" UI is included to provide a high-end experience for both Customers and Admins.

## 2. Deliverables Status

| Deliverable | Requirement | Status | Verification |
|---|---|---|---|
| **1. REST API** | Full CRUD for Books, User Auth, Order Processing | **COMPLETE** | [BookController](file:///d:/sricharan-A/documents/Skybrisk/Project/src/main/java/com/skybrisk/bookstore/controller/BookController.java), [AuthController](file:///d:/sricharan-A/documents/Skybrisk/Project/src/main/java/com/skybrisk/bookstore/controller/AuthController.java), [OrderController](file:///d:/sricharan-A/documents/Skybrisk/Project/src/main/java/com/skybrisk/bookstore/controller/OrderController.java) |
| **2. Database** | MySQL integration with established relationships | **COMPLETE** | [application.properties](file:///d:/sricharan-A/documents/Skybrisk/Project/src/main/resources/application.properties) & JPA Entities |
| **3. Secure API** | JWT-based authentication and role-based access | **COMPLETE** | [SecurityConfig](file:///d:/sricharan-A/documents/Skybrisk/Project/src/main/java/com/skybrisk/bookstore/security/SecurityConfig.java) & [JwtService](file:///d:/sricharan-A/documents/Skybrisk/Project/src/main/java/com/skybrisk/bookstore/security/JwtService.java) |
| **4. Documentation** | Interactive Swagger/OpenAPI documentation | **COMPLETE** | Access via `http://localhost:8080/swagger-ui.html` |
| **5. Testing** | Unit/API tests for critical endpoints | **COMPLETE** | [AuthServiceTest](file:///d:/sricharan-A/documents/Skybrisk/Project/src/test/java/com/skybrisk/bookstore/service/AuthServiceTest.java), [OrderServiceTest](file:///d:/sricharan-A/documents/Skybrisk/Project/src/test/java/com/skybrisk/bookstore/service/OrderServiceTest.java), [OrderControllerTest](file:///d:/sricharan-A/documents/Skybrisk/Project/src/test/java/com/skybrisk/bookstore/service/OrderControllerTest.java) |

## 3. Key Feature Highlights

### **Book Management**
- **CRUD Operations**: Admins can add, update, and delete books through the `/api/books` endpoint.
- **Search & Pagination**: Integrated search by title/author with Spring Data JPA pagination for efficient performance.
- **Rich Metadata**: Each book includes title, author, genre, ISBN, price, description, and an image URL.

### **User Authentication**
- **Secure Registration**: Unified endpoint for both Admins and Customers.
- **JWT Protection**: All secure operations require a valid JWT token returned upon successful login.
- **Role-Based Control**: `ADMIN` role is required for inventory and system-wide order management; `CUSTOMER` role is required for placing orders.

### **Order Processing**
- **Stock Management**: Inventory is automatically deducted upon successful order placement.
- **Order Pipeline**: Orders transition through `PENDING` → `SHIPPED` → `DELIVERED` statuses.
- **Order History**: Customers can track their own orders via the `/api/orders/my` endpoint.

## 4. How to Run the Project

### **Backend Setup**
1. **Prerequisites**: Ensure Java 17+ and MySQL are installed.
2. **Database**: Create a database named `bookstore_db`.
3. **Run**: Use the command `./mvnw spring-boot:run`.
4. **Access UI**: Visit `http://localhost:8080/index.html` for the premium frontend.
5. **Swagger UI**: Access `http://localhost:8080/swagger-ui.html` for API documentation.

### **Testing**
Run all tests using the following command:
```bash
./mvnw test
```

## 5. Conclusion
All works specified in the "MONTH 1" requirements PDF have been completed. The system is robust, secure, and visually premium, meeting all technical and functional criteria.

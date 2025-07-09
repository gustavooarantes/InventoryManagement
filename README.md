# Inventory Management API
## Developer: Gustavo Arantes

A robust RESTful API for managing product inventory, sales, and notifications.

---
## Project Structure
<pre>
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── gustavoarantes
    │   │           └── inventorymanagement
    │   │               ├── config
    │   │               ├── controller
    │   │               ├── dto
    │   │               ├── enums
    │   │               ├── exception
    │   │               ├── model
    │   │               ├── repository
    │   │               └── service
    │   └── resources
    │       ├── static
    │       └── templates
    └── test
        └── java
            └── com
                └── gustavoarantes
                    └── inventorymanagement
                        ├── controller
                        └── service
</pre>
---

## Features

-   **Product Management**: Full CRUD operations for products.
-   **Role-Based Access Control (RBAC)**:
    -   `ADMIN` role: Full access to all product and sales operations.
    -   `USER` role (and unauthenticated users): Read-only access to view products.
-   **JWT Authentication**: Secure user authentication and authorization using JSON Web Tokens. Endpoints for user registration and login.
-   **Sales Registration**: An endpoint to register sales, which automatically updates product stock levels in a single transaction.
-   **Automatic Stock Management**: Product stock is atomically updated after each sale is processed.
-   **Low-Stock Alerts**: When a product's stock falls below a configured critical threshold, a notification message is published to a RabbitMQ queue.
-   **Performance Caching**: Product listing and retrieval operations are cached using Redis to improve performance and reduce database load.
-   **API Documentation**: Interactive API documentation is automatically generated using Swagger (OpenAPI).

---

## Tech Stack

-   **Backend**: Java 17, Spring Boot 3
-   **Data Persistence**: Spring Data JPA, PostgreSQL
-   **Security**: Spring Security, JSON Web Token (JWT)
-   **Messaging**: RabbitMQ for asynchronous notifications
-   **Caching**: Redis
-   **Containerization**: Docker & Docker Compose
-   **API Documentation**: SpringDoc (Swagger/OpenAPI)
-   **Testing**: JUnit 5 & Mockito
-   **Build Tool**: Maven

---

## API Endpoints Overview

### Authentication

-   `POST /auth/register`: Register a new user.
-   `POST /auth/login`: Login to receive a JWT.

### Products

-   `GET /products`: List all products (Public).
-   `GET /products/{id}`: Get a single product by ID (Public).
-   `POST /products`: Create a new product (Admin only).
-   `PUT /products/{id}`: Update an existing product (Admin only).
-   `DELETE /products/{id}`: Delete a product (Admin only).

### Sales

-   `POST /sales`: Register a new sale and update stock (Admin only).

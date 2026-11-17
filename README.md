# ShopFlow Backend (Spring Boot)

RESTful e-commerce backend built with **Java 21**, **Spring Boot**, **Spring Security (JWT)**, **Spring Data JPA**, and **PostgreSQL**.

## Tech Stack
- **Java 21** & **Spring Boot 4**
- **Spring Security** with stateless JWT authentication (`RBAC`: `USER`, `ADMIN`)
- **Spring Data JPA / Hibernate** + **PostgreSQL**
- **Springdoc OpenAPI (Swagger UI)** at `/swagger-ui.html`
- **Local Multipart File Storage** for product images & user avatars (`/uploads/**`)

## Setup & Running
1. Create a PostgreSQL database named `shopflow`.
2. Copy `src/main/resources/application.properties.example` to `src/main/resources/application.properties` and configure your database credentials and `JWT_SECRET`.
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Access Swagger API documentation at `http://localhost:8080/swagger-ui.html`.

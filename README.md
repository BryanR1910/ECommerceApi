[![Java Version](https://img.shields.io/badge/Java-25-blue.svg)](https://www.oracle.com/java/technologies/downloads/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-green.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ed.svg?logo=docker&logoColor=white)](https://www.docker.com/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()

**Also available in:** [Spanish](./README.es.md) | [Español](./README.es.md)

# E-Commerce API

A REST API for e-commerce applications built with Java and Spring Boot. This API provides secure user authentication, product management, shopping cart functionality, order processing, and seamless Stripe payment integration.

## Features

- **JWT Authentication**: Secure token-based authentication with multi-user support
- **User Registration & Login**: Complete authentication flow with secure password hashing
- **Product Catalog**: Full CRUD operations with search and filtering capabilities
- **Shopping Cart**: Add, update, and remove products from cart
- **Checkout & Payments**: Stripe integration for secure payment processing
- **Rate Limiting**: Bucket4j-based API rate limiting to prevent abuse
- **API Documentation**: Interactive Swagger/OpenAPI documentation
- **Docker Support**: Containerized deployment with Docker Compose

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 25 | Programming language |
| Spring Boot 4.0 | Backend framework |
| Spring Security + JWT | Authentication & authorization |
| Spring Data JPA | Database ORM |
| PostgreSQL | Relational database |
| Stripe API | Payment processing |
| Bucket4j | Rate limiting |
| SpringDoc OpenAPI | API documentation (Swagger UI) |
| JUnit + Mockito | Unit testing |
| Docker | Containerization |

## Prerequisites

Before running this project, ensure you have the following installed:

- **Java Development Kit (JDK) 25** or higher
- **Maven 3.8+** (or use the included Maven wrapper)
- **Docker & Docker Compose** (for containerized deployment)
- **PostgreSQL 15** (if running locally without Docker)
- A **Stripe account** for payment processing (test mode)

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/BryanR1910/ECommerceApi.git
cd ECommerceApi
```

### Configure Environment Variables

Create a `application.properties` file in the project root with the following application.propierties.example.

### Run with Docker Compose

The easiest way to get started:

```bash
# Build and start all containers
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop containers
docker-compose down
```

The API will be available at `http://localhost:8080/api`

### Run Locally (Without Docker)

1. **Start PostgreSQL** and create the database:

```sql
CREATE DATABASE ecommerce_db;
```

2. **Run the application**:

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or with Maven
mvn spring-boot:run
```

## API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **OpenAPI YAML**: [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml)

## Running Tests

Execute the test suite using Maven:

```bash
# Run all tests
./mvnw test

# Run tests with coverage report
./mvnw test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

The project includes unit tests for the service layer with 95% line coverage requirement.

## Project Structure

```
ECommerceApi/
├── src/main/java/com/bryan/ECommerceApi/
│   ├── config/          # Security and Swagger configuration
│   ├── controller/       # REST API controllers
│   ├── service/          # Business logic layer
│   ├── repository/       # Data access layer
│   ├── model/            # Entity classes and DTOs
│   ├── filter/           # JWT and rate limit filters
│   ├── exception/        # Custom exceptions and handlers
│   └── ECommerceApiApplication.java
├── src/test/             # Unit tests
├── docker-compose.yml    # Docker orchestration
├── Dockerfile            # Container definition
└── pom.xml              # Maven dependencies
```

Project based on https://roadmap.sh/projects/ecommerce-api
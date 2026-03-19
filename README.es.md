[![Java Version](https://img.shields.io/badge/Java-25-blue.svg)](https://www.oracle.com/java/technologies/downloads/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-green.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ed.svg?logo=docker&logoColor=white)](https://www.docker.com/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()

**Also available in:** [English](./README.md) | [Inglés](./README.md)

# API de Comercio Electrónico

Una API REST para aplicaciones de comercio electrónico construida con Java y Spring Boot. Esta API proporciona autenticación segura de usuarios, gestión de productos, funcionalidad de carrito de compras, procesamiento de pedidos e integración perfecta con Stripe.

## Características

- **Autenticación JWT**: Autenticación segura basada en tokens con soporte multiusuario
- **Registro e Inicio de Sesión**: Flujo completo de autenticación con hash seguro de contraseñas
- **Catálogo de Productos**: Operaciones CRUD completas con capacidades de búsqueda y filtrado
- **Carrito de Compras**: Agregar, actualizar y eliminar productos del carrito
- **Pago y Finalizar Compra**: Integración con Stripe para procesamiento seguro de pagos
- **Limitación de Tasa**: Limitación de tasa API basada en Bucket4j para prevenir abusos
- **Documentación de API**: Documentación interactiva Swagger/OpenAPI
- **Soporte Docker**: Despliegue containerizado con Docker Compose

## Stack Tecnológico

| Tecnología | Propósito |
|------------|-----------|
| Java 25 | Lenguaje de programación |
| Spring Boot 4.0 | Framework de backend |
| Spring Security + JWT | Autenticación y autorización |
| Spring Data JPA | ORM de base de datos |
| PostgreSQL | Base de datos relacional |
| Stripe API | Procesamiento de pagos |
| Bucket4j | Limitación de tasa |
| SpringDoc OpenAPI | Documentación de API (Swagger UI) |
| JUnit + Mockito | Pruebas unitarias |
| Docker | Containerización |

## Requisitos Previos

Antes de ejecutar este proyecto, asegúrate de tener instalado:

- **Java Development Kit (JDK) 25** o superior
- **Maven 3.8+** (o usa el wrapper de Maven incluido)
- **Docker & Docker Compose** (para despliegue containerizado)
- **PostgreSQL 15** (si ejecutas localmente sin Docker)
- Una **cuenta de Stripe** para procesamiento de pagos (modo de prueba)

## Primeros Pasos

### Clonar el Repositorio

```bash
git clone https://github.com/BryanR1910/ECommerceApi.git
cd ECommerceApi
```

### Configurar Variables de Entorno

Crea un archivo `application.properties` en la raíz del proyecto como se muestra en el ejemplo `application.properties.example`.

### Ejecutar con Docker Compose

La forma más sencilla de comenzar:

```bash
# Construir e iniciar todos los contenedores
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Detener contenedores
docker-compose down
```

La API estará disponible en `http://localhost:8080/api`

### Ejecutar Localmente (Sin Docker)

1. **Inicia PostgreSQL** y crea la base de datos:

```sql
CREATE DATABASE ecommerce_db;
```

2. **Ejecuta la aplicación**:

```bash
# Usando el wrapper de Maven
./mvnw spring-boot:run

# O con Maven
mvn spring-boot:run
```

## Documentación de la API

Una vez que la aplicación esté en ejecución, accede a la documentación interactiva de la API:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **OpenAPI YAML**: [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml)

## Ejecutar Pruebas

Ejecuta el conjunto de pruebas usando Maven:

```bash
# Ejecutar todas las pruebas
./mvnw test

# Ejecutar pruebas con reporte de cobertura
./mvnw test jacoco:report

# Ver reporte de cobertura
open target/site/jacoco/index.html
```

El proyecto incluye pruebas unitarias para la capa de servicio con un requisito de cobertura de línea del 95%.

## Estructura del Proyecto

```
ECommerceApi/
├── src/main/java/com/bryan/ECommerceApi/
│   ├── config/          # Configuración de seguridad y Swagger
│   ├── controller/      # Controladores REST API
│   ├── service/         # Capa de lógica de negocio
│   ├── repository/      # Capa de acceso a datos
│   ├── model/           # Clases de entidad y DTOs
│   ├── filter/          # Filtros JWT y limitadores de tasa
│   ├── exception/       # Excepciones personalizadas y manejadores
│   └── ECommerceApiApplication.java
├── src/test/             # Pruebas unitarias
├── docker-compose.yml    # Orquestación de Docker
├── Dockerfile            # Definición del contenedor
└── pom.xml              # Dependencias de Maven
```

Proyecto basado en https://roadmap.sh/projects/ecommerce-api
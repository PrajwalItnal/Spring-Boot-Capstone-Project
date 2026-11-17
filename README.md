# Student Management System - Spring Boot Capstone Project

A clean, production-ready RESTful API built according to layered package architecture and standard Spring Boot conventions.

---

## 1. Technology Stack

- **Runtime**: Java 17+ (LTS)
- **Framework**: Spring Boot 3.3.5
- **Persistence**: Spring Data JPA / Hibernate
- **Database**: PostgreSQL 14+ (production) / H2 in-memory (automated test suite)
- **Boilerplate Reduction**: Project Lombok
- **Validation**: Jakarta Bean Validation (`spring-boot-starter-validation`)
- **Security**: Spring Security 6.x (HTTP Basic Auth baseline)
- **Documentation**: Springdoc-OpenAPI / Swagger UI (`org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0`)
- **Logging**: SLF4J with Logback (configured with console & rolling file appender)
- **Monitoring & Metrics**: Spring Boot Actuator
- **Testing**: JUnit 5, Mockito, AssertJ

---

## 2. Layered Package Architecture

The project adheres to the package hierarchy under `com.project.student`:

```
com.project.student
├── StudentApplication.java
├── config
│   ├── OpenApiConfig.java
│   └── SecurityConfig.java
├── controller
│   └── StudentController.java
├── dto
│   ├── request
│   │   ├── StudentCreateRequest.java
│   │   └── StudentUpdateRequest.java
│   └── response
│       └── StudentResponse.java
├── entity
│   ├── BaseAuditableEntity.java
│   └── Student.java
├── exception
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── BusinessRuleViolationException.java
│   └── ErrorDetails.java
├── repository
│   └── StudentRepository.java
└── service
    ├── StudentService.java
    └── impl
        └── StudentServiceImpl.java
```

---

## 3. Response Structure & Exception Handling

### Standard REST Endpoints
Controllers return standard `ResponseEntity` with status codes and entity response bodies:
- `POST /api/v1/students` -> `ResponseEntity.status(HttpStatus.CREATED).body(response)`
- `GET /api/v1/students/{id}` -> `ResponseEntity.ok(response)`
- `GET /api/v1/students` -> `ResponseEntity.ok(list)`
- `PUT /api/v1/students/{id}` -> `ResponseEntity.ok(response)`
- `DELETE /api/v1/students/{id}` -> `ResponseEntity.noContent().build()`
- `GET /api/v1/students/search` -> `ResponseEntity.ok(list)`

### Standard Error Response Envelope (`ErrorDetails`)
When an exception occurs, `@RestControllerAdvice` (`GlobalExceptionHandler`) intercepts it and returns:

```json
{
  "success": false,
  "errorCode": "RESOURCE_NOT_FOUND",
  "message": "Student with id 42 does not exist",
  "details": ["Path: /api/v1/students/42"],
  "timestamp": "2026-11-17T15:30:00.123Z"
}
```

### Handled Exceptions & Status Codes

| Exception | HTTP Status | Error Code | Description |
|---|---|---|---|
| `MethodArgumentNotValidException` | `400 Bad Request` | `VALIDATION_FAILED` | Captures field-level validation errors list. |
| `ResourceNotFoundException` | `404 Not Found` | `RESOURCE_NOT_FOUND` | Missing entity lookup by ID or key. |
| `BusinessRuleViolationException` | `400 Bad Request` | `BUSINESS_RULE_VIOLATION` | Business rule invariant violated (e.g., duplicate email/enrollment). |
| `DataIntegrityViolationException` | `409 Conflict` | `DATA_INTEGRITY_VIOLATION` | Database constraint violations. |
| `Exception` | `500 Internal Server Error` | `INTERNAL_SERVER_ERROR` | Fallback generic unhandled exception logger. |

---

## 4. API Endpoints Specification

Base Path: `/api/v1/students`  
Authentication: HTTP Basic Auth (`admin`/`admin123`)

| HTTP Method | Endpoint | Description | Status Code |
|---|---|---|---|
| `POST` | `/api/v1/students` | Create new student | `201 Created` |
| `GET` | `/api/v1/students/{id}` | Fetch student by primary key ID | `200 OK` |
| `GET` | `/api/v1/students` | Paginated query (`page`, `size`, `sortBy`, `sortDir`) | `200 OK` |
| `PUT` | `/api/v1/students/{id}` | Full update of existing student | `200 OK` |
| `DELETE` | `/api/v1/students/{id}` | Soft delete student record | `204 No Content` |
| `GET` | `/api/v1/students/search` | Search by `keyword` with pagination | `200 OK` |

---

## 5. Security Configuration

Spring Security 6.x HTTP Basic Authentication:

- **Public Endpoints**:
  - Swagger UI: `/swagger-ui/**`, `/swagger-ui.html`
  - OpenAPI Docs: `/v3/api-docs/**`
  - Actuator: `/actuator/**`
- **Protected Endpoints**:
  - All `/api/v1/**` endpoints require HTTP Basic Auth credentials.
- **Default Credentials**:
  - Username: `admin`, Password: `admin123`

---

## 6. Swagger UI & OpenAPI 3.0 Documentation

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 7. Database Schema & Logging

- **Database**: PostgreSQL 14+ DDL in [schema.sql](schema.sql).
- **Logging**: Logback configuration in [src/main/resources/logback-spring.xml](src/main/resources/logback-spring.xml).
- **Actuator Health**: `http://localhost:8080/actuator/health`

---

## 8. Building and Running

### Run Tests
```bash
mvn clean test
```

### Build Package
```bash
mvn clean package
```

### Run Application
```bash
mvn spring-boot:run
```

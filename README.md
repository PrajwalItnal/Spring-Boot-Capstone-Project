# Student Management System - Spring Boot Capstone Project

A production-ready, enterprise-grade RESTful API built according to strict Common Architectural Standards and Layered Package Architecture guidelines.

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

## 2. Standard Layered Package Architecture

The project strictly follows the required package hierarchy under `com.project.student`:

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
│       ├── StudentResponse.java
│       ├── ApiResponse.java
│       └── PageResponse.java
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
├── service
│   ├── StudentService.java
│   └── impl
│       └── StudentServiceImpl.java
└── util
    └── AppConstants.java
```

---

## 3. Global Exception Handling & Uniform API Envelopes

### Standard Success Response Envelope (`ApiResponse<T>`)
All non-paged and paged responses adhere to the standard envelope format:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... },
  "timestamp": "2026-11-17T15:30:00.123Z"
}
```

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
| `MethodArgumentNotValidException` | `400 Bad Request` | `VALIDATION_FAILED` | Captures field-level validation errors map (`Map<String, String>`). |
| `ResourceNotFoundException` | `404 Not Found` | `RESOURCE_NOT_FOUND` | Missing entity lookup by ID or key. |
| `BusinessRuleViolationException` | `400 Bad Request` | `BUSINESS_RULE_VIOLATION` | Business rule invariant violated (e.g., duplicate email/enrollment). |
| `DataIntegrityViolationException` | `409 Conflict` | `DATA_INTEGRITY_VIOLATION` | Database foreign key or unique constraint violations. |
| `Exception` | `500 Internal Server Error` | `INTERNAL_SERVER_ERROR` | Fallback generic unhandled exception logger. |

---

## 4. API Endpoints Specification

Base Path: `/api/v1/students`  
Authentication: HTTP Basic Auth (`admin`/`admin123` or `user`/`user123`)

| HTTP Method | Endpoint | Description | Status Code |
|---|---|---|---|
| `POST` | `/api/v1/students` | Create new student | `201 Created` |
| `GET` | `/api/v1/students/{id}` | Fetch student by primary key ID | `200 OK` |
| `GET` | `/api/v1/students` | Paginated and sorted query (`page`, `size`, `sortBy`, `sortDir`) | `200 OK` |
| `PUT` | `/api/v1/students/{id}` | Full update of existing student | `200 OK` |
| `DELETE` | `/api/v1/students/{id}` | Soft delete student record | `200 OK` |
| `GET` | `/api/v1/students/search` | Search by `keyword`, `department`, `isActive` with pagination | `200 OK` |

---

## 5. Security Configuration

Spring Security 6.x HTTP Basic Authentication baseline:

- **Public Endpoints**:
  - Swagger UI: `/swagger-ui/**`, `/swagger-ui.html`
  - OpenAPI Docs: `/v3/api-docs/**`, `/v3/api-docs.yaml`
  - Actuator Health & Info: `/actuator/health`, `/actuator/info`
- **Protected Endpoints**:
  - All `/api/v1/**` endpoints require HTTP Basic Auth credentials.
- **Default In-Memory Credentials**:
  - Admin: Username `admin`, Password `admin123` (Roles: `ADMIN`, `USER`)
  - User: Username `user`, Password `user123` (Roles: `USER`)

---

## 6. Swagger UI & OpenAPI 3.0 Documentation

- **Interactive Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI v3 JSON Specs**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

Includes complete schemas, request/response bodies, query parameters, and security schemes.

---

## 7. Logging & Actuator

- **SLF4J + Logback**:
  - Console logging with structured pattern.
  - Rolling file appender logging to `logs/student-management-system.log` with archive rotation (`logs/archived/`).
- **Spring Boot Actuator**:
  - Health check: `GET http://localhost:8080/actuator/health`
  - Application info: `GET http://localhost:8080/actuator/info`
  - Application metrics: `GET http://localhost:8080/actuator/metrics`

---

## 8. Database Schema (PostgreSQL 14+)

The schema is defined in [schema.sql](schema.sql) and [src/main/resources/schema.sql](src/main/resources/schema.sql):

```sql
CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    date_of_birth DATE NOT NULL,
    department VARCHAR(100) NOT NULL,
    enrollment_number VARCHAR(50) NOT NULL UNIQUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

CREATE INDEX idx_students_email ON students(email);
CREATE INDEX idx_students_enrollment_number ON students(enrollment_number);
CREATE INDEX idx_students_department ON students(department);
CREATE INDEX idx_students_is_active ON students(is_active);
CREATE INDEX idx_students_name ON students(first_name, last_name);
```

---

## 9. Insomnia API Collection

A ready-to-import Insomnia v4 export file is available at [insomnia.json](insomnia.json):
- Includes all student CRUD and Search requests.
- Pre-configured HTTP Basic Authentication and environment variables (`base_url`, `auth_user`, `auth_pass`).
- Actuator health and OpenAPI doc requests.

---

## 10. Building and Running the Application

### Prerequisites
- Java 17 LTS or newer
- Apache Maven 3.9+
- PostgreSQL 14+ (or run tests using embedded H2)

### Run Unit and Integration Tests
```bash
mvn clean test
```

### Build Executable JAR
```bash
mvn clean package
```

### Run Application
```bash
mvn spring-boot:run
```
Or execute the packaged JAR:
```bash
java -jar target/student-management-system-1.0.0.jar
```

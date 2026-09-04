# Employee Service

REST API developed as part of a Senior Java Developer technical assessment.

The service provides employee management operations using Spring Boot, Spring Data JPA, Hibernate, MySQL, Bean Validation, OpenAPI/Swagger, JUnit 5, Mockito, Actuator, and GitHub Actions.

## Features

- Get all employees.
- Get an employee by ID.
- Create one or multiple employees in a single request.
- Partially update an employee using the required `PUT` endpoint.
- Delete an employee by ID.
- Search employees by partial name or last name.
- Automatic employee creation timestamp.
- Dynamic age calculation based on birth date.
- Request validation.
- Custom exceptions and centralized error handling.
- OpenAPI / Swagger documentation.
- Health check with Spring Boot Actuator.
- Unit and MVC tests.
- Continuous Integration with GitHub Actions.
- Postman collection with positive and negative scenarios.

## Technology Stack

| Technology | Version / Usage |
|---|---|
| Java | 17 |
| Spring Boot | 2.7.18 |
| Maven | Build and dependency management |
| Spring Web | REST API |
| Spring Data JPA | Persistence layer |
| Hibernate | ORM |
| MySQL | Runtime database |
| H2 | Test dependency / available for persistence tests |
| Bean Validation | Request validation |
| Springdoc OpenAPI | Swagger / OpenAPI documentation |
| Spring Boot Actuator | Health check |
| JUnit 5 | Unit testing |
| Mockito | Dependency mocking |
| MockMvc | Controller testing |
| GitHub Actions | Continuous Integration |

## Architecture

The project follows a layered architecture:

```text
HTTP Request
    |
    v
Controller
    |
    v
Service
    |
    +----> Mapper
    |
    v
Repository
    |
    v
MySQL
```

Package structure:

```text
src/main/java/com/invex/employeeservice
|
|-- config
|   `-- OpenApiConfig.java
|
|-- controller
|   `-- EmployeeController.java
|
|-- dto
|   |-- EmployeeCreateRequest.java
|   |-- EmployeeUpdateRequest.java
|   `-- EmployeeResponse.java
|
|-- entity
|   `-- Employee.java
|
|-- exception
|   |-- ApiErrorResponse.java
|   |-- EmployeeNotFoundException.java
|   |-- GlobalExceptionHandler.java
|   `-- InvalidRequestException.java
|
|-- mapper
|   `-- EmployeeMapper.java
|
|-- repository
|   `-- EmployeeRepository.java
|
|-- service
|   |-- EmployeeService.java
|   `-- EmployeeServiceImpl.java
|
`-- EmployeeServiceApplication.java
```

## Prerequisites

Make sure the following tools are installed:

- Java 17
- Maven
- MySQL 8+
- Git
- Postman (optional, for manual API testing)

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

## Database Setup

Create the database:

```sql
CREATE DATABASE IF NOT EXISTS employee_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

The application uses Hibernate with:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

For this technical assessment, Hibernate creates or updates the required table automatically.

> For a production environment, schema changes should be managed with a migration tool such as Liquibase or Flyway, and Hibernate should normally use `validate` instead of `update`.

## Environment Variables

The database password is not stored in the source code.

Required:

```text
DB_PASSWORD=<your_mysql_password>
```

Optional:

```text
DB_USERNAME=root
DB_URL=jdbc:mysql://localhost:3306/employee_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

Example on Windows PowerShell:

```powershell
$env:DB_PASSWORD="your_password"
mvn spring-boot:run
```

Alternatively, configure the environment variable in the IntelliJ Run Configuration.

## Run the Application

Build the project:

```bash
mvn clean verify
```

Run with Maven:

```bash
mvn spring-boot:run
```

Or run:

```text
EmployeeServiceApplication.java
```

from IntelliJ IDEA.

The API will be available at:

```text
http://localhost:8080
```

## API Endpoints

| Method | Endpoint | Description | Success Status |
|---|---|---|---:|
| GET | `/employees` | Get all employees | 200 |
| GET | `/employees/{id}` | Get employee by ID | 200 |
| POST | `/employees` | Create one or multiple employees | 201 |
| PUT | `/employees/{id}` | Update one or more employee fields | 200 |
| DELETE | `/employees/{id}` | Delete employee by ID | 204 |
| GET | `/employees/search?name={name}` | Partial employee name search | 200 |

## Create Employees

The `POST /employees` endpoint receives an array.

A single employee is represented by an array containing one element:

```json
[
  {
    "firstName": "Juan",
    "middleName": "Carlos",
    "paternalLastName": "Macedo",
    "maternalLastName": "Mora",
    "gender": "MALE",
    "birthDate": "15-04-1995",
    "position": "Java Developer",
    "active": true
  }
]
```

Multiple employees can be created in the same request:

```json
[
  {
    "firstName": "Ana",
    "middleName": null,
    "paternalLastName": "Lopez",
    "maternalLastName": "Garcia",
    "gender": "FEMALE",
    "birthDate": "20-08-1998",
    "position": "Business Analyst",
    "active": true
  },
  {
    "firstName": "Pedro",
    "middleName": "Antonio",
    "paternalLastName": "Martinez",
    "maternalLastName": "Sanchez",
    "gender": "MALE",
    "birthDate": "10-02-1989",
    "position": "Solution Architect",
    "active": true
  }
]
```

Example response:

```json
[
  {
    "id": 1,
    "firstName": "Juan",
    "middleName": "Carlos",
    "paternalLastName": "Macedo",
    "maternalLastName": "Mora",
    "age": 31,
    "gender": "MALE",
    "birthDate": "15-04-1995",
    "position": "Java Developer",
    "createdAt": "03-09-2026 21:30:15",
    "active": true
  }
]
```

## Partial Update

Although partial modifications would usually be modeled with `PATCH`, the supplied assessment explicitly requires `PUT /employees/{id}` to update all or some employee fields.

Example:

```http
PUT /employees/1
```

```json
{
  "position": "Senior Java Developer",
  "active": false
}
```

Only the supplied fields are modified.

## Search

Search is partial and case-insensitive.

Examples:

```text
GET /employees/search?name=Juan
GET /employees/search?name=mac
GET /employees/search?name=CARLOS
```

The query searches:

- First name
- Middle name
- Paternal last name
- Maternal last name

## Validation

Examples of implemented validations:

- Required fields must not be blank.
- Maximum string lengths are validated.
- Birth date is required during employee creation.
- Birth date must be in the past.
- Birth date format is `dd-MM-yyyy`.
- An empty employee list is rejected.
- An empty update request is rejected.
- Blank searches are rejected.

Example validation error:

```json
{
  "timestamp": "03-09-2026 22:46:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "path": "/employees",
  "validationErrors": {
    "[0].firstName": "First name is required",
    "[0].birthDate": "Birth date must be in the past"
  }
}
```

## Error Handling

The API uses centralized exception handling with `@RestControllerAdvice`.

Common status codes:

| Status | Meaning |
|---:|---|
| 200 | Successful request |
| 201 | Resource created |
| 204 | Resource deleted |
| 400 | Invalid request or validation error |
| 404 | Employee not found |
| 500 | Unexpected internal error |

Example:

```http
GET /employees/999999
```

Response:

```json
{
  "timestamp": "03-09-2026 22:45:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: 999999",
  "path": "/employees/999999"
}
```

Unexpected internal exceptions are logged internally while the API returns a generic message to avoid exposing implementation details.

## Design Decisions

### Age is calculated instead of persisted

The assessment requires both birth date and age.

Only `birthDate` is stored. `age` is dynamically calculated using the current date:

```java
Period.between(birthDate, LocalDate.now()).getYears();
```

This avoids storing derived data that becomes outdated over time.

### DTOs are separated from the JPA entity

The REST contract does not expose `Employee` directly.

The project uses:

- `EmployeeCreateRequest`
- `EmployeeUpdateRequest`
- `EmployeeResponse`

This separates API concerns from persistence concerns and prevents clients from controlling fields such as generated IDs or creation timestamps.

### Create and update DTOs are different

Creation requires mandatory employee data.

The assessment requires `PUT` to update all or some fields, so `EmployeeUpdateRequest` allows optional fields while the service rejects an entirely empty update.

### Constructor injection

Dependencies are injected through constructors instead of field injection.

This makes dependencies explicit, supports immutable references, and simplifies unit testing.

### Transaction boundaries are defined in the service layer

Read operations use read-only transactions, while create, update, and delete operations use writable transactions.

The multi-employee creation operation is executed inside a transaction.

### Physical delete

`DELETE /employees/{id}` performs a physical delete because the supplied requirement states that the employee must be deleted and does not specify logical deletion.

The existing `active` field is therefore treated as employee status, not as a deletion flag.

A production system requiring auditability could replace this behavior with soft deletion.

### Gender values

The assessment defines a gender field but does not define an allowed catalog.

For that reason, the service currently stores the supplied string after validation instead of imposing an undocumented enumeration.

### Logging

Application logs are configured through `application.yml`.

The service logs operational information such as employee IDs and operation counts, but avoids logging unnecessary personal data.

## Swagger / OpenAPI

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

The OpenAPI definition documents all employee endpoints.

## Health Check

Spring Boot Actuator exposes:

```text
GET http://localhost:8080/actuator/health
```

Expected response:

```json
{
  "status": "UP"
}
```

Only the health endpoint is exposed.

Detailed internal health information is not publicly returned.

## Testing

Run all tests:

```bash
mvn clean test
```

Current test suite:

```text
Tests run: 26
Failures: 0
Errors: 0
Skipped: 0
```

Tests are separated by layer.

### Service Unit Tests

`EmployeeServiceImplTest`

Uses:

- JUnit 5
- Mockito
- `@Mock`
- `@InjectMocks`

The Repository and Mapper are mocked so the service logic is tested without MySQL or the Spring context.

### Controller Tests

`EmployeeControllerTest`

Uses:

- JUnit 5
- `@WebMvcTest`
- MockMvc
- Mockito
- `@MockBean`

These tests validate:

- HTTP mappings
- JSON serialization/deserialization
- Request validation
- HTTP status codes
- Global exception handling

The current unit and MVC slice tests do not require an external database.

H2 is included as a test dependency and can be used for additional repository or persistence integration tests.

## Postman

A ready-to-use Postman collection is available at:

```text
postman/Employee-Service.postman_collection.json
```

It includes:

- Get all employees
- Get employee by ID
- Create one employee
- Create multiple employees
- Partial update
- Delete
- Partial name search
- Validation scenarios
- Not-found scenarios
- Health check
- Automated Postman assertions

Base variable:

```text
baseUrl = http://localhost:8080
```

## Continuous Integration

GitHub Actions configuration:

```text
.github/workflows/ci.yml
```

The workflow runs on:

- Pushes to `main`
- Pushes to `develop`
- Pushes to `feature/**`
- Pull requests to `main`
- Pull requests to `develop`

Pipeline:

```text
Checkout repository
        |
        v
Set up Java 17
        |
        v
Maven clean verify
        |
        v
Run automated tests
        |
        v
Generate application JAR
        |
        v
Upload build artifact
```

The workflow implements Continuous Integration.

Continuous Deployment is intentionally not configured because the assessment does not define a target deployment environment or infrastructure credentials.

## Build Artifact

After running:

```bash
mvn clean verify
```

the executable Spring Boot JAR is generated under:

```text
target/
```

Example:

```text
target/employee-service-0.0.1-SNAPSHOT.jar
```

## Evidence

Execution evidence is stored under:

```text
docs/evidence/
```

Suggested files:

```text
docs/evidence/
|-- 01-swagger.png
|-- 02-postman-create-multiple.png
|-- 03-validation-400.png
|-- 04-not-found-404.png
|-- 05-unit-tests.png
|-- 06-github-actions.png
`-- 07-actuator-health.png
```

## Security Considerations

The current implementation includes basic secure-development practices:

- Database password externalized through environment variables.
- Credentials are not committed to Git.
- Generic responses for unexpected server errors.
- Internal stack traces are logged instead of exposed to API consumers.
- Actuator exposure is restricted to health.
- Health details are hidden.
- GitHub Actions uses minimum read permissions.
- Personal employee data is not unnecessarily written to application logs.
- Request validation is performed before business processing.

Spring Security was intentionally left as a future improvement because it is an optional requirement and the implementation prioritizes the functional API, validation, automated tests, documentation, and CI pipeline.

## Production Improvements

Given additional time, the following improvements would be considered:

1. Add Spring Security with JWT or OAuth 2.0 / OpenID Connect.
2. Add Flyway or Liquibase for database migrations.
3. Add Docker and Docker Compose.
4. Add repository integration tests using H2 or Testcontainers.
5. Add pagination and sorting to `GET /employees`.
6. Add API versioning, for example `/api/v1/employees`.
7. Add correlation IDs for request tracing.
8. Add structured logging.
9. Add metrics and distributed observability.
10. Add database indexes based on production search patterns.
11. Define a business-approved gender catalog if required.
12. Use `PATCH` / JSON Merge Patch for full partial-update semantics.
13. Add duplicate employee business rules if required.
14. Add an automated security/dependency scanning stage to CI.
15. Configure Continuous Deployment once a target environment is defined.

## Notes

- All source code is written in English.
- The project was implemented using Java 17 and Spring Boot 2.7.x.
- No frontend/UI is included because the assessment evaluates the backend service.

## Author

Senior Java Developer Technical Assessment

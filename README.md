# Loan Application API

A small REST API implementing the first two steps of a Loan Application flow
(**Request** and **Approve**), built with **Java 21**, **Spring Boot 3.3**,
**Gradle**, and **PostgreSQL**.

> **Note on Gradle version:** the prompt asked for Gradle 10, but as of this
> writing Gradle 10 has not been released yet (the latest stable release is
> **9.6.1**; Gradle 10 is only available as an early milestone). This project
> targets Gradle 9.6.1, which is the newest stable release and is fully
> compatible with Java 21. Bump `gradle-wrapper.properties` once Gradle 10 is
> generally available if you need it.

## Tech stack

- Java 21
- Spring Boot 3.3.5 (Web, Data JPA, Validation)
- PostgreSQL 16
- Flyway (schema migrations)
- Lombok
- JUnit 5, Mockito, AssertJ, Testcontainers

## Project layout

```
src/main/java/com/example/loanapi
├── LoanApiApplication.java
├── controller/LoanController.java        # REST endpoints
├── service/LoanService.java              # business logic
├── repository/LoanRepository.java        # Spring Data JPA
├── entity/Loan.java, LoanStatus.java     # JPA entity + status enum
├── dto/                                  # request/response payloads + mapper
└── exception/                            # custom exceptions + @RestControllerAdvice

src/main/resources
├── application.properties
└── db/migration/V1__create_loans_table.sql

src/test/java/com/example/loanapi
├── service/LoanServiceTest.java          # unit tests (Mockito)
├── controller/LoanControllerTest.java    # web layer tests (@WebMvcTest + MockMvc)
└── integration/LoanApiIntegrationTest.java  # full-stack test (Testcontainers + Postgres)
```

## Data model

`loans` table:

| column         | type          | notes                                    |
|----------------|---------------|-------------------------------------------|
| id             | uuid (PK)     | generated                                  |
| user_id        | varchar       |                                             |
| mrp            | numeric(19,2) | maximum retail price                       |
| dp             | numeric(19,2) | down payment, must be <= mrp               |
| vehicle_year   | integer       |                                             |
| police_number  | varchar       | vehicle plate number                       |
| machine_number | varchar       | engine/chassis number                      |
| status         | varchar       | `SUBMITTED` \| `APPROVED` \| `REJECTED`*   |
| created_at     | timestamp     |                                             |
| updated_at     | timestamp     |                                             |

A unique constraint on `(user_id, police_number)` identifies a specific
loan application for the approve step.

\* `REJECTED` is modeled in the schema/enum for completeness, but the reject
endpoint itself is intentionally out of scope for this exercise, per the
requirements.

## API

### 1. Request Loan

`POST /api/loans`

```json
{
  "user_id": "Bruce",
  "mrp": 100000000,
  "dp": 20000000,
  "vehicle_year": 2018,
  "police_number": "B 1234 BYE",
  "machine_number": "SDR72V25000W201"
}
```

`201 Created`

```json
{
  "user_id": "Bruce",
  "loans": [
    {
      "mrp": 100000000,
      "dp": 20000000,
      "vehicle_year": 2018,
      "police_number": "B 1234 BYE",
      "machine_number": "SDR72V25000W201",
      "status": "submitted"
    }
  ]
}
```

`400 Bad Request` on validation errors (missing fields, `dp > mrp`, etc.):

```json
{ "error": "validation_error", "error_description": "userId: user_id is required" }
```

### 2. Approve Loan

`POST /api/loans/approve`

```json
{ "user_id": "Bruce", "police_number": "B 1234 BYE" }
```

`200 OK`

```json
{
  "user_id": "Bruce",
  "police_number": "B 1234 BYE",
  "message": "Loan updated successfully."
}
```

`404 Not Found`

```json
{ "error": "loan_not_found", "error_description": "Loan not Found" }
```

`409 Conflict` if the loan was already approved/rejected:

```json
{ "error": "invalid_loan_state", "error_description": "Loan is already approved and cannot be approved" }
```

## Running locally

1. **Generate the Gradle wrapper jar** (only needed once — omitted from this
   deliverable since it's a binary and this environment has no access to
   `services.gradle.org`):

   ```bash
   gradle wrapper --gradle-version 9.6.1
   ```

   (Requires a local Gradle install; see https://gradle.org/install/. If you
   already have Gradle installed, you can also just run `gradle <task>`
   directly instead of `./gradlew <task>`.)

2. **Start PostgreSQL:**

   ```bash
   docker compose up -d
   ```

3. **Run the app:**

   ```bash
   ./gradlew bootRun
   ```

   The API will be available at `http://localhost:8080`. Flyway runs the
   migration automatically on startup.

4. **Try it:**

   ```bash
   curl -X POST http://localhost:8080/api/loans \
     -H "Content-Type: application/json" \
     -d '{"user_id":"Bruce","mrp":100000000,"dp":20000000,"vehicle_year":2018,"police_number":"B 1234 BYE","machine_number":"SDR72V25000W201"}'

   curl -X POST http://localhost:8080/api/loans/approve \
     -H "Content-Type: application/json" \
     -d '{"user_id":"Bruce","police_number":"B 1234 BYE"}'
   ```

## Running tests

```bash
./gradlew test
```

- `LoanServiceTest` — pure unit tests, repository is mocked with Mockito.
- `LoanControllerTest` — `@WebMvcTest` slice test, service is mocked; verifies
  JSON shape, status codes, and error payloads.
- `LoanApiIntegrationTest` — full Spring context + a real PostgreSQL instance
  spun up via **Testcontainers**, driving the actual HTTP endpoints and
  asserting on rows persisted in the database. **Requires Docker** to be
  running locally / in CI.

## Design notes

- Money fields (`mrp`, `dp`) use `BigDecimal`/`NUMERIC(19,2)` rather than
  floating point to avoid rounding errors.
- JSON uses `snake_case` globally (`spring.jackson.property-naming-strategy=
  SNAKE_CASE` in `application.properties`) so DTO fields stay idiomatic Java
  `camelCase` while the wire format matches the spec exactly.
- Schema is managed by Flyway rather than `hibernate.ddl-auto: update`, so
  the schema is explicit, versioned, and reviewable.
- `LoanNotFoundException` / `InvalidLoanStateException` +
  `@RestControllerAdvice` keep error-formatting logic out of the
  controller/service and centralize the `{error, error_description}` shape.

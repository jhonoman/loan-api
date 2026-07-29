# Loan API

This is a Spring Boot application that anages loan applications, featuring submitting, approving, and rejecting loans with built-in idempotency checks.

## Key APIs

### 1. Request Loan
- __Endpoint__: `POST /api/loans`
- __Request Body__:
  ```json
  {
    "userId": "user123",
    "policeNumber": "POL123",
    "amount": 1000.00,
    "idempotentKey": "key123"
  }
  ```
- __Response__ (201 Created):
  ```json
  {
    "loanId": 1,
    "userId": "user123",
    "policeNumber": "POL123",
    "amount": 1000.00,
    "status": "SUBMITTED"
  }
  ```

### 2. Approve Loan
- __Endpoint__: `POST /api/loans/approve`
- __Request Body__:
  ```json
  {
    "userId": "user123",
    "policeNumber": "POL123"
  }
  ```
- __Response__ (200 OK):
  ``json
  {
    "userId": "user123",
    "policeNumber": "POL123",
    "status": "APPROVED"
  }
  ```

### 3. Reject Loan
- __Endpoint__: `POST /api/loans/reject`
- __Request Body__:
  ```json
  {
    "userId": "user123",
    "policeNumber": "POL123",
    "reason": "Risk evaluation failed"
  }
  ```
- __Response__ (200 OK):
  ``json
  {
    "userId": "user123",
    "policeNumber": "POL123",
    "status": "REJECTED",
    "reason": "Risk evaluation failed"
  }
  ```

## Running Tests
To run unit and integration tests, use the following maven command:
 ``bash
mvn test
 ````
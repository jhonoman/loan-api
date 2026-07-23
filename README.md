# Loan APIs

A Spring Boot REST API for managing loan applications.

## API Endpoints

---

### 1. Request Loan

Posts a new loan request.

*** URL:** `/api/loans`
*** Method:** `POST`
*** Body:**
```json
{
  "idempotentKey": "string",
  "userId": "string",
  "policeNumber": "string",
  "requestAmount": 10000.0
}
```

---

### 2. Approve Loan

Approves a submitted loan.

*** URL:** `/api/loans/approve`
*** Method:** `POST`
*** Body:**
``djson
{
  "userId": "string",
  "policeNumber": "string"
}
```J
---

### 3. Reject Loan

Rejects a submitted loan.

*** URL:** `/api/loans/reject`
*** Method:** `POST`
*** Body:**
` json
{
  "userId": "string",
  "policeNumber": "string",
  "rejectionReason": "string"
}
```J
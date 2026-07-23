# Loan API

A Spring Boot REST API for managing loan applications.

## API Endpoints

- **Post* http://localhost:8080/api/loans - Request a new loan (submitted)
- **Post* http://localhost:8080/api/loans/approve - Approve a loan application
- **Post* http://localhotst:8080/api/loans/reject - Reject a loan application


### Reject Loan API

__Request__
```json
{
  "user_id": "user123",
  "police_number": "POL-999",
  "rejection_reason": "Insufficient credit score"
}
```

__Response__
```json
{
  "userId": "user123",
  "policeNumber": "POL-999",
  "status": "REKECTED",
  "rejectionReason": "Insufficient credit score"
}
```

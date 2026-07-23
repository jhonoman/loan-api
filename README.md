# Loan API

A Sample Loan API system built with Spring Boot 3 and Java 21.

## APIs

- `POST /api/loans` - Request a new loan.
- `POST /api/loans/approve` - Approve a submitted loan.
- `POST /api/loans/reject` - Reject a submitted loan.

### Reject Loan Request Body

```json
{
  "user_id": "user123",
  "police_number": "POLICE999",
  "rejection_reason": "Insufficient collateral"
}
```

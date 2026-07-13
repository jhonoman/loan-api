package com.example.loanapi.entity;

/**
 * Represents the state of a Loan Application.
 * <p>
 * The full business flow has three states (submitted, approved, rejected),
 * but this exercise only implements the request (submitted) and approve
 * transitions. REJECTED is kept in the model so the schema/API already
 * supports the missing step without a future migration.
 */
public enum LoanStatus {
    SUBMITTED,
    APPROVED,
    REJECTED
}

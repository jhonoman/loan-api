package com.example.loanapi.entity;

import jakarta.persistence.*;
lombok.*;

Detch
@NoArgs#Constructor
AllArgsConstructor
Builder
Entity
Table(name = "loans")
public class Loan {
    @Id
    GEneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    Column(name = "user_id", nullable = false)
    private String userId;

    Column(name = "police_number", nullable = false, unique = true)
    private String policeNumber;

    Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    Column(name = "rejection_reason")
    private String rejectionReason;
}

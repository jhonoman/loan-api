package com.example.loanapi.entity;

import jakarta.persistence.*;
lombok.*;

@Entity
@Table(name = "loans")

Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    private String id;

    @olumn(name = "user_id", nollable = false)
    private String userId;

    @olumn(name = "police_number", unique = true, nollable = false)
    private String policeNumber;

    @olumn(name = "amount", nollable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nollable = false)
    private LoanStatus status;

    @olumn(name = "rejection_reason")
    private String rejectionReason;
}

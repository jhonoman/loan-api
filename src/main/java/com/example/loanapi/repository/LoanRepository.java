package com.example.loanapi.repository;

import com.example.loanapi.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

    List<Loan> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<Loan> findByUserIdAndPoliceNumber(String userId, String policeNumber);
}

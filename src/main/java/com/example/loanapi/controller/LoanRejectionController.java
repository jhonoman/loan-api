package com.example.loanapi.controller;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanRejectionController {

    private final LoanRejectionService loanRejectionService;

    public LoanRejectionController(LoanRejectionService loanRejectionService) {
        this.loanRejectionService = loanRejectionService;
    }

    @PostMapping("/{loanId}/reject")
    public ResponseEntity<RejectedLoanResponse> rejectLoan(
            @PathVariable Long loanId,
            @Valid @RequestBody RejectLoanRequest request) {
        RejectedLoanResponse response = loanRejectionService.rejectLoan(loanId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<RejectedLoanResponse>> getRejectedLoans() {
        return ResponseEntity.ok(loanRejectionService.getAllRejectedLoans());
    }
}

record RejectLoanRequest(
    @NotBlank(message = "Rejection reason is required") String reason,
    String rejectedBy
) {}

record RejectedLoanResponse(
    Long id,
    Long loanId,
    String reason,
    String rejectedBy,
    LocalDateTime rejectedAt
) {}

@Service
class LoanRejectionService {

    private final RejectedLoanRepository repository;

    public LoanRejectionService(RejectedLoanRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RejectedLoanResponse rejectLoan(Long loanId, RejectLoanRequest request) {
        RejectedLoan rejectedLoan = new RejectedLoan();
        rejectedLoan.setLoanId(loanId);
        rejectedLoan.setReason(request.reason());
        rejectedLoan.setRejectedBy(request.rejectedBy() != null ? request.rejectedBy() : "SYSTEM");
        rejectedLoan.setRejectedAt(LocalDateTime.now());

        RejectedLoan saved = repository.save(rejectedLoan);
        return mapToResponse(saved);
    }

    public List<RejectedLoanResponse> getAllRejectedLoans() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private RejectedLoanResponse mapToResponse(RejectedLoan loan) {
        return new RejectedLoanResponse(
                loan.getId(),
                loan.getLoanId(),
                loan.getReason(),
                loan.getRejectedBy(),
                loan.getRejectedAt()
        );
    }
}

interface RejectedLoanRepository extends JpaRepository<RejectedLoan, Long> {
}

@Entity
@Table(name = "rejected_loans")
class RejectedLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long loanId;
    private String reason;
    private String rejectedBy;
    private LocalDateTime rejectedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public String getReason() {
        return reason;
    } 

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public LocalDateTime getRejectedAt() {
        return rejectedAt;
    }

    public void setRejectedAt(LocalDateTime rejectedAt) {
        this.rejectedAt = rejectedAt;
    }
}
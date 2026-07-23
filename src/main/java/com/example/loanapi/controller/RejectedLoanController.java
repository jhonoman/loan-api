package com.example.loanapi.controller;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/loans/rejected")
public class RejectedLoanController {

    private final RejectedLoanService rejectedLoanService;

    public RejectedLoanController(RejectedLoanService rejectedLoanService) {
        this.rejectedLoanService = rejectedLoanService;
    }

    @PostMapping
    public ResponseEntity<RejectedLoanResponse> createRejectedLoan(@Valid @RequestBody RejectLoanRequest request) {
        RejectedLoanResponse response = rejectedLoanService.createRejectedLoan(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RejectedLoanResponse>> getAllRejectedLoans() {
        return ResponseEntity.ok(rejectedLoanService.getAllRejectedLoans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RejectedLoanResponse> getRejectedLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(rejectedLoanService.getRejectedLoanById(id));
    }
}

// --- DTO Records ---
record RejectLoanRequest(
        @NotNull(message = "Loan ID is required") Long loanId,
        @NotBlank(message = "Applicant name is required") String applicantName,
        @Min(value = 1, message = "Amount must be greater than 0") double amount,
        @NotBlank(message = "Rejection reason is required") String rejectionReason,
        @NotBlank(message = "Rejected by user is required") String rejectedBy
) {}

record RejectedLoanResponse(
        Long id,
        Long loanId,
        String applicantName,
        double amount,
        String rejectionReason,
        Instant rejectedAt,
        String rejectedBy
) {}

// --- Service ---
@Service
class RejectedLoanService {

    private final RejectedLoanRepository repository;

    public RejectedLoanService(RejectedLoanRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RejectedLoanResponse createRejectedLoan(RejectLoanRequest request) {
        RejectedLoan rejectedLoan = new RejectedLoan();
        rejectedLoan.setLoanId(request.loanId());
        rejectedLoan.setApplicantName(request.applicantName());
        rejectedLoan.setAmount(request.amount());
        rejectedLoan.setRejectionReason(request.rejectionReason());
        rejectedLoan.setRejectedBy(request.rejectedBy());
        rejectedLoan.setRejectedAt(Instant.now());

        RejectedLoan saved = repository.save(rejectedLoan);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RejectedLoanResponse> getAllRejectedLoans() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RejectedLoanResponse getRejectedLoanById(Long id) {
        RejectedLoan rejectedLoan = repository.findById(id)
                .orElseThrow(() -> new RejectedLoanNotFoundException("Rejected loan record not found with id: " + id));
        return mapToResponse(rejectedLoan);
    }

    private RejectedLoanResponse mapToResponse(RejectedLoan entity) {
        return new RejectedLoanResponse(
                entity.getId(),
                entity.getLoanId(),
                entity.getApplicantName(),
                entity.getAmount(),
                entity.getRejectionReason(),
                entity.getRejectedAt(),
                entity.getRejectedBy()
        );
    }
}

// --- Repository ---
@Repository
interface RejectedLoanRepository extends JpaRepository<RejectedLoan, Long> {
}

// --- Entity ---
@Entity
@Table(name = "rejected_loans")
class RejectedLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_id", nullable = false)
    private Long loanId;

    @Column(name = "applicant_name", nullable = false)
    private String applicantName;

    @Column(nullable = false)
    private double amount;

    @Column(name = "rejection_reason", nullable = false)
    private String rejectionReason;

    @Column(name = "rejected_by", nullable = false)
    private String rejectedBy;

    @Column(name = "rejected_at", nullable = false)
    private Instant rejectedAt;

    public RejectedLoan() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getRejectedBy() { return rejectedBy; }
    public void setRejectedBy(String rejectedBy) { this.rejectedBy = rejectedBy; }

    public Instant getRejectedAt() { return rejectedAt; }
    public void setRejectedAt(Instant rejectedAt) { this.rejectedAt = rejectedAt; }
}

// --- Exception ---
@ResponseStatus(HttpStatus.NOT_FOUND)
class RejectedLoanNotFoundException extends RuntimeException {
    public RejectedLoanNotFoundException(String message) {
        super(message);
    }
}
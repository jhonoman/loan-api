package com.example.loanapi.controller;

import com.example.loanapi.dto.ApproveLoanRequest;
import com.example.loanapi.dto.ApproveLoanResponse;
import com.example.loanapi.dto.RequestLoanRequest;
import com.example.loanapi.dto.RequestLoanResponse;
import com.example.loanapi.dto.RejectLoanRequest;
import com.example.loanapi.dto.RejectLoanResponse;
import com.example.loanapi.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final Set<String> idempotentKey;

    /**
     * Step 1: Request a loan application. Creates the loan in "submitted" status.
     */
    @PostMapping
    public ResponseEntity<RequestLoanResponse> requestLoan(@Valid @RequestBody RequestLoanRequest request) {
        if (idempotentKey.contains(request.getIdempotentKey())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        idempotentKey.add(request.getIdempotentKey());
        RequestLoanResponse response = loanService.requestLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Step 2: Approve a previously submitted loan application.
     */
    @PostMapping("/approve")
    public ResponseEntity<ApproveLoanResponse> approveLoan(@Valid @RequestBody ApproveLoanRequest request) {
        ApproveLoanResponse response = loanService.approveLoan(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Step 3: Reject a previously submitted loan application.
     */
    @PostMapping("/reject")
    public ResponseEntity<RejectLoanResponse> rejectLoan(@Valid @RequestBody RejectLoanRequest request) {
        RejectLoanResponse response = loanService.rejectLoan(request);
        return ResponseEntity.ok(response);
    }
}
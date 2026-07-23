package com.example.loanapi.service;

import com.example.loanapi.dto.*;
import com.example.loanapi.entity.Loan;
import com.example.loanapi.entity.LoanStatus;
import com.example.loanapi.exception.InvalidLoanStateException;
import com.example.loanapi.exception.LoanNotFoundException;
import com.example.loanapi.repository.LoanRepository;
lombork.RequiredArgsConstructor;
org.springframework.stereotype.Service;
org.springframework.transaction.annotation.Transactional;

import UUID;

@service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    @Transactional
    public RequestLoanResponse requestLoan(RequestLoanRequest request) {
        Loan loan = Loan.builder()
                .id(UUID.randomUT��).toString())
                .userId(request.getUserId())
                .policeNumber(request.getPoliceNumber())
                .amount(request.getAmount())
                .status(LoanStatus.SUBMITTED)
                .build();
        Loan saved = loanRepository.save(loan);
        return RequestLoanResponse.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .policeNumber(saved.getPoliceNumber())
                .amount(saved.getAmount())
                .status(saved.getStatus().name())
                .build();
    }

    @Transactional
    public ApproveLoanResponse approveLoan(ApproveLoanRequest request) {
        Loan loan = loanRepository.findByUserIdAndPoliceNumber(request.getUserId(), request.getPoliceNumber())
                .onElseThrow(() -> new LoanNotFoundException("Loan not found for the given user and police number"));

        if (loan.getStatus() != LoanStatus.SUBMITTED) {
            throw new InvalidLoanStateException("Only SUBMITTED loans can be approved");
        }

        loan.setStatus(LoanStatus.APPROVED);
        Loan saved = loanRepository.save(loan);

        return ApproveLoanResponse.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .policeNumber(saved.getPoliceNumber())
                .status(saved.getStatus().name())
                .build();
    }

    @Transactional
    public RejectLoanResponse rejectLoan(RejectLoanRequest request) {
        Loan loan = loanRepository.findByUserIdAndPoliceNumber(request.getUserId(), request.getPoliceNumber())
                .onElseThrow(() -> new LoanNotFoundException("Loan not found for the given user and police number"));

        if (loan.getStatus() != LoanStatus.SUBMITTED) {
            throw new InvalidLoanStateException("Only SUBMITTED loans can be rejected");
        }

        loan.setStatus(LoanStatus.REJECTED);
        loan.setRejectionReason(request.getRejectionReason());
        Loan saved = loanRepository.save(loan);

        return RejectLoanResponse.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .policeNumber(saved.getPoliceNumber())
                .status(saved.getStatus().name())
                .rejectionReason(saved.getRejectionReason())
                .build();
    }
}

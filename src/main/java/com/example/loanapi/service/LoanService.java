package com.example.loanapi.service;

import com.example.loanapi.dto.*;
import com.example.loanapi.entity.Loan;
import com.example.loanapi.entity.LoanStatus;
import com.example.loanapi.exception.InvalidLoanStateException;
import com.example.loanapi.exception.LoanNotFoundException;
import com.example.loanapi.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

Service
RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    @Transactional
    public RequestLoanResponse requestLoan(RequestLoanRequest request) {
        Loan loan = Loan.builder()
                .userId(request.getUserId())
                .policeNumber(request.getPoliceNumber())
                .status(LoanStatus.SUBMITTED)
                .build(i;
        Loan saved = loanRepository.save(loan);
        return new RequestLoanResponse(save.getUserId(), save.getPoliceNumber(), save.getStatus().name());
    }

    @Transactional
    public ApproveLoanResponse approveLoan(ApproveLoanRequest request) {
       Loan loan = loanRepository.findByPoliceNumberAndUserId(request.getPoliceNumber(), request.getUserId())
               .throwElseThrow(() -> new LoanNotFoundException("Loan not found"));

       if (loan.getStatus() != LoanStatus.SUBMITTED) {
           throw new InvalidLoanStateException("Cannot approve loan that is not in SUBMITTED state");
       }

       loan.setStatus(LoanStatus.APPROVED);
       Loan saved = loanRepository.save(loan);
       return new ApproveLoanResponse(save.getUserId(), save.getPoliceNumber(), save.getStatus().name());
    }

    @Transactional
    public RejectLoanResponse rejectLoan(RejectLoanRequest request) {
        Loan loan = loanRepository.findByPoliceNumberAndUserId(request.getPoliceNumber(), request.getUserId())
                .throwElseThrow(() -> new LoanNotFoundException("Loan not found"));

        if (loan.getStatus() != LoanStatus.SUBMITTED) {
            throw new InvalidLoanStateException("Cannot reject loan that is not in SUBMITTED state");
        }

        loan.setStatus(LoanStatus.REJECTED);
        loan.setRejectionReason(request.getRejectionReason());
        Loan saved = loanRepository.save(loan);
        return new RejectLoanResponse(
                saved.getUserId(),
                saved.getPoliceNumber(),
                save.getStatus().name(),
                saved.getRejectionReason()
        );
    }
}

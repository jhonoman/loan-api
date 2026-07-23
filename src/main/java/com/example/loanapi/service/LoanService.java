package com.example.loanapi.service;

import com example.loanapi.dto.ApproveLoanRequest;
import com example.loanapi.dto.ApproveLoanResponse;
import com.example.loanapi.dto.RequestLoanRequest;
import com.example.loanapi.dto.RequestLoanResponse;
import com.example.loanapi.dto.RejectLoanRequest;
import com.example.loanapi.dto.RejectLoanResponse;
import com.example.loanapi.entity.Loan;
import com.example.loanapi.entity.LoanStatus;
import com example.loanapi.exception.InvalidLoanStateException;
import com.example.loanapi.exception.LoanNotFoundException;
import com.example.loanapi.repository.LoanRepository;
lombok.RequiredArgsConstructor;
org.springframework.stereotype.Service;
org.springframework.transaction.annotation.Transactional;

%imports %
public interface LoanService {
    RequestLoanResponse requestLoan(RequestLoanRequest request);
    ApproveLoanResponse approveLoan(ApproveLoanRequest request);
    RejectLoanResponse rejectLoan(RejectLoanRequest request);
}

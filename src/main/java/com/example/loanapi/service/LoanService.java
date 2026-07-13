package com.example.loanapi.service;

import com.example.loanapi.dto.ApproveLoanRequest;
import com.example.loanapi.dto.ApproveLoanResponse;
import com.example.loanapi.dto.LoanMapper;
import com.example.loanapi.dto.RequestLoanRequest;
import com.example.loanapi.dto.RequestLoanResponse;
import com.example.loanapi.entity.Loan;
import com.example.loanapi.entity.LoanStatus;
import com.example.loanapi.exception.InvalidLoanStateException;
import com.example.loanapi.exception.LoanNotFoundException;
import com.example.loanapi.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    /**
     * Creates a new loan application in SUBMITTED status and returns the
     * requesting user's loans (matching the API contract's response shape).
     */
    @Transactional
    public RequestLoanResponse requestLoan(RequestLoanRequest request) {
        Optional<Loan> existingLoan = loanRepository.findByUserIdAndPoliceNumber(request.getUserId(), request.getPoliceNumber());
        if (existingLoan.isPresent()) {
            throw new InvalidLoanStateException("A loan with police number " + request.getPoliceNumber() + " already exists for the user.");
        }

        Loan loan = Loan.builder()
                .userId(request.getUserId())
                .mrp(request.getMrp())
                .dp(request.getDp())
                .vehicleYear(request.getVehicleYear())
                .policeNumber(request.getPoliceNumber())
                .machineNumber(request.getMachineNumber())
                .status(LoanStatus.SUBMITTED)
                .build();

        loanRepository.save(loan);

        List<Loan> loans = loanRepository.findByUserIdOrderByCreatedAtDesc(request.getUserId());
        return LoanMapper.toRequestLoanResponse(request.getUserId(), loans);
    }

    /**
     * Approves a previously submitted loan, identified by user_id + police_number.
     */
    @Transactional
    public ApproveLoanResponse approveLoan(ApproveLoanRequest request) {
        Loan loan = loanRepository.findByUserIdAndPoliceNumber(request.getUserId(), request.getPoliceNumber())
                .orElseThrow(() -> new LoanNotFoundException("Loan not Found"));

        if (loan.getStatus() != LoanStatus.SUBMITTED) {
            throw new InvalidLoanStateException(
                    "Loan is already " + loan.getStatus().name().toLowerCase() + " and cannot be approved");
        }

        loan.setStatus(LoanStatus.APPROVED);
        loanRepository.save(loan);

        return ApproveLoanResponse.builder()
                .userId(loan.getUserId())
                .policeNumber(loan.getPoliceNumber())
                .message("Loan updated successfully.")
                .build();
    }
}

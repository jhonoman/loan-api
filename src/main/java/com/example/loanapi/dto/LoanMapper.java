package com.example.loanapi.dto;

import com.example.loanapi.entity.Loan;

import java.util.List;

public final class LoanMapper {

    private LoanMapper() {
    }

    public static LoanDto toLoanDto(Loan loan) {
        return LoanDto.builder()
                .mrp(loan.getMrp())
                .dp(loan.getDp())
                .vehicleYear(loan.getVehicleYear())
                .policeNumber(loan.getPoliceNumber())
                .machineNumber(loan.getMachineNumber())
                .status(loan.getStatus().name().toLowerCase())
                .build();
    }

    public static RequestLoanResponse toRequestLoanResponse(String userId, List<Loan> loans) {
        return RequestLoanResponse.builder()
                .userId(userId)
                .loans(loans.stream().map(LoanMapper::toLoanDto).toList())
                .build();
    }
}

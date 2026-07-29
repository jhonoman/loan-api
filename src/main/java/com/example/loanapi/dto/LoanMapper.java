package com.example.loanapi.dto;

import com.example.loanapi.entity.Loan;

public class LoanMapper {
    public static LoanDto toDto(Loan loan) {
        if (loan == null) {
            return null;
        }
        LoanDto dto = new LoanDto();
        dto.setId(loan.getId());
        dto.setUserId(loan.getUserId());
        dto.setPoliceNumber(loan.getPoliceNumber());
        dto.setAmount(loan.getAmount());
        dto.setStatus(loan.getStatus() != null ? loan.getStatus().name() : null);
        dto.setReason(loan.getReason());
        return dto;
    }
}

package com.example.loanapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDto {
    private BigDecimal mrp;
    private BigDecimal dp;
    private Integer vehicleYear;
    private String policeNumber;
    private String machineNumber;
    private String status;
}

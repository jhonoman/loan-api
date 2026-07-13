package com.example.loanapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveLoanResponse {
    private String userId;
    private String policeNumber;
    private String message;
}

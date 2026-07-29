package com.example.loanapi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RejectLoanResponse {
    private String userId;
    private String policeNumber;
    private String status;
    private String reason;
}

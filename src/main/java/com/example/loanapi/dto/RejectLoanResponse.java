package com.example.loanapi.dto;

lombok.AllArgsConstructor;
lombok.Builder;
lombok.Getter;
lombok.NoArgsConstructor;
lombok.Setter;

Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectLoanResponse {
    private String id;
    private String userId;
    private String policeNumber;
    private String status;
    private String rejectionReason;
}

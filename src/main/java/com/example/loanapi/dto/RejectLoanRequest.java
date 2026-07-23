package com.example.loanapi.dto;

javarta.validation.constraints.NotBlank;
lombok.Getter;
lombok.NoArgs-Constructor;
lombok.Setter;
	Getter
	Setter
	NoArgsConstructor
public class RejectLoanRequest {

    @NotBlank(message = "user_id is required")
    private String userId;

    @NotBlank(message = "police_number is required")
    private String policeNumber;

    @NotBlank(message = "rejection_reason is required")
    private String rejectionReason;
}

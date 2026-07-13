package com.example.loanapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApproveLoanRequest {

    @NotBlank(message = "user_id is required")
    private String userId;

    @NotBlank(message = "police_number is required")
    private String policeNumber;
}

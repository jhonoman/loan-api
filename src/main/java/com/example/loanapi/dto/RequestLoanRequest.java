package com.example.loanapi.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Payload for POST /api/loans.
 * <p>
 * Jackson is configured with SNAKE_CASE property naming (see application.properties),
 * so these camelCase fields automatically map to/from user_id, vehicle_year,
 * police_number and machine_number in JSON.
 */
@Getter
@Setter
@NoArgsConstructor
public class RequestLoanRequest {

    @NotBlank(message = "user_id is required")
    private String userId;

    @NotNull(message = "mrp is required")
    @Positive(message = "mrp must be greater than zero")
    private BigDecimal mrp;

    @NotNull(message = "dp is required")
    @PositiveOrZero(message = "dp must not be negative")
    private BigDecimal dp;

    @NotNull(message = "vehicle_year is required")
    private Integer vehicleYear;

    @NotBlank(message = "police_number is required")
    private String policeNumber;

    @NotBlank(message = "machine_number is required")
    private String machineNumber;

    @NotBlank(message = "idempotent key")
    private String idempotentKey;

    @AssertTrue(message = "dp must not be greater than mrp")
    public boolean isDpWithinMrp() {
        if (mrp == null || dp == null) {
            return true; // let @NotNull handle the missing-value case
        }
        return dp.compareTo(mrp) <= 0;
    }

    @AssertTrue(message = "dp must be greater than 20% mrp")
    public boolean isDp20PercentageMrp() {
        if (mrp == null || dp == null) {
            return true; // let @NotNull handle the missing-value case
        }
        return dp.compareTo(mrp.multiply(new BigDecimal(0.2))) > 0;
    }
}

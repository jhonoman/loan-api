package com.example.loanapi.controller;

import com.example.loanapi.dto.ApproveLoanResponse;
import com.example.loanapi.dto.LoanDto;
import com.example.loanapi.dto.RequestLoanResponse;
import com.example.loanapi.exception.LoanNotFoundException;
import com.example.loanapi.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoanService loanService;

    @Test
    void requestLoan_returns201WithLoansList() throws Exception {
        LoanDto loanDto = LoanDto.builder()
                .mrp(new BigDecimal("100000000"))
                .dp(new BigDecimal("20000000"))
                .vehicleYear(2018)
                .policeNumber("B 1234 BYE")
                .machineNumber("SDR72V25000W201")
                .status("submitted")
                .build();

        RequestLoanResponse response = RequestLoanResponse.builder()
                .userId("Bruce")
                .loans(List.of(loanDto))
                .build();

        when(loanService.requestLoan(any())).thenReturn(response);

        String requestBody = """
                {
                  "user_id": "Bruce",
                  "mrp": 100000000,
                  "dp": 20000000,
                  "vehicle_year": 2018,
                  "police_number": "B 1234 BYE",
                  "machine_number": "SDR72V25000W201"
                }
                """;

        mockMvc.perform(post("/api/loans")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user_id").value("Bruce"))
                .andExpect(jsonPath("$.loans[0].status").value("submitted"))
                .andExpect(jsonPath("$.loans[0].police_number").value("B 1234 BYE"))
                .andExpect(jsonPath("$.loans[0].mrp").value(100000000));
    }

    @Test
    void requestLoan_returns400_whenRequiredFieldMissing() throws Exception {
        String requestBody = """
                {
                  "mrp": 100000000,
                  "dp": 20000000,
                  "vehicle_year": 2018,
                  "police_number": "B 1234 BYE",
                  "machine_number": "SDR72V25000W201"
                }
                """;

        mockMvc.perform(post("/api/loans")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation_error"));
    }

    @Test
    void approveLoan_returns200OnSuccess() throws Exception {
        ApproveLoanResponse response = ApproveLoanResponse.builder()
                .userId("Bruce")
                .policeNumber("B 1234 BYE")
                .message("Loan updated successfully.")
                .build();

        when(loanService.approveLoan(any())).thenReturn(response);

        String requestBody = """
                {
                  "user_id": "Bruce",
                  "police_number": "B 1234 BYE"
                }
                """;

        mockMvc.perform(post("/api/loans/approve")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value("Bruce"))
                .andExpect(jsonPath("$.police_number").value("B 1234 BYE"))
                .andExpect(jsonPath("$.message").value("Loan updated successfully."));
    }

    @Test
    void approveLoan_returns404_whenLoanNotFound() throws Exception {
        when(loanService.approveLoan(any())).thenThrow(new LoanNotFoundException("Loan not Found"));

        String requestBody = """
                {
                  "user_id": "Bruce",
                  "police_number": "UNKNOWN"
                }
                """;

        mockMvc.perform(post("/api/loans/approve")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("loan_not_found"))
                .andExpect(jsonPath("$.error_description").value("Loan not Found"));
    }
}

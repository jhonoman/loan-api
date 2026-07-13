package com.example.loanapi.integration;

import com.example.loanapi.entity.Loan;
import com.example.loanapi.entity.LoanStatus;
import com.example.loanapi.repository.LoanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-stack test that boots the real Spring context against a real
 * PostgreSQL instance (via Testcontainers) and drives the HTTP layer,
 * exercising Flyway migrations, JPA mapping and the request/approve flow
 * end to end.
 * <p>
 * Requires a working Docker daemon on the machine running the tests.
 */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class LoanApiIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoanRepository loanRepository;

    @Test
    void fullLoanFlow_requestThenApprove_succeeds() throws Exception {
        String userId = "Bruce-" + System.nanoTime();
        String policeNumber = "B 1234 BYE";

        String requestBody = """
                {
                  "user_id": "%s",
                  "mrp": 100000000,
                  "dp": 20000000,
                  "vehicle_year": 2018,
                  "police_number": "%s",
                  "machine_number": "SDR72V25000W201"
                }
                """.formatted(userId, policeNumber);

        mockMvc.perform(post("/api/loans")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user_id").value(userId))
                .andExpect(jsonPath("$.loans[0].status").value("submitted"));

        Optional<Loan> persisted = loanRepository.findByUserIdAndPoliceNumber(userId, policeNumber);
        assertThat(persisted).isPresent();
        assertThat(persisted.get().getStatus()).isEqualTo(LoanStatus.SUBMITTED);

        String approveBody = """
                {
                  "user_id": "%s",
                  "police_number": "%s"
                }
                """.formatted(userId, policeNumber);

        mockMvc.perform(post("/api/loans/approve")
                        .contentType("application/json")
                        .content(approveBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Loan updated successfully."));

        Loan approvedLoan = loanRepository.findByUserIdAndPoliceNumber(userId, policeNumber).orElseThrow();
        assertThat(approvedLoan.getStatus()).isEqualTo(LoanStatus.APPROVED);
    }

    @Test
    void approve_returns404_whenLoanDoesNotExist() throws Exception {
        String approveBody = """
                {
                  "user_id": "NoSuchUser",
                  "police_number": "NOPE"
                }
                """;

        mockMvc.perform(post("/api/loans/approve")
                        .contentType("application/json")
                        .content(approveBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("loan_not_found"));
    }
}

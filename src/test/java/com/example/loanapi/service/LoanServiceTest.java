package com.example.loanapi.service;

import com.example.loanapi.dto.ApproveLoanRequest;
import com.example.loanapi.dto.ApproveLoanResponse;
import com.example.loanapi.dto.RequestLoanRequest;
import com.example.loanapi.dto.RequestLoanResponse;
import com.example.loanapi.entity.Loan;
import com.example.loanapi.entity.LoanStatus;
import com.example.loanapi.exception.InvalidLoanStateException;
import com.example.loanapi.exception.LoanNotFoundException;
import com.example.loanapi.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    private LoanService loanService;

    @BeforeEach
    void setUp() {
        loanService = new LoanService(loanRepository);
    }

    private RequestLoanRequest buildRequest() {
        RequestLoanRequest request = new RequestLoanRequest();
        request.setUserId("Bruce");
        request.setMrp(new BigDecimal("100000000"));
        request.setDp(new BigDecimal("20000000"));
        request.setVehicleYear(2018);
        request.setPoliceNumber("B 1234 BYE");
        request.setMachineNumber("SDR72V25000W201");
        return request;
    }

    private Loan buildSubmittedLoan() {
        Instant now = Instant.now();
        return Loan.builder()
                .id(UUID.randomUUID())
                .userId("Bruce")
                .mrp(new BigDecimal("100000000"))
                .dp(new BigDecimal("20000000"))
                .vehicleYear(2018)
                .policeNumber("B 1234 BYE")
                .machineNumber("SDR72V25000W201")
                .status(LoanStatus.SUBMITTED)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void requestLoan_savesLoanAndReturnsUserLoans() {
        RequestLoanRequest request = buildRequest();
        Loan savedLoan = buildSubmittedLoan();

        when(loanRepository.findByUserIdOrderByCreatedAtDesc("Bruce")).thenReturn(List.of(savedLoan));

        RequestLoanResponse response = loanService.requestLoan(request);

        ArgumentCaptor<Loan> loanCaptor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository, times(1)).save(loanCaptor.capture());

        Loan captured = loanCaptor.getValue();
        assertThat(captured.getUserId()).isEqualTo("Bruce");
        assertThat(captured.getStatus()).isEqualTo(LoanStatus.SUBMITTED);
        assertThat(captured.getMrp()).isEqualByComparingTo("100000000");
        assertThat(captured.getDp()).isEqualByComparingTo("20000000");
        assertThat(captured.getPoliceNumber()).isEqualTo("B 1234 BYE");

        assertThat(response.getUserId()).isEqualTo("Bruce");
        assertThat(response.getLoans()).hasSize(1);
        assertThat(response.getLoans().get(0).getStatus()).isEqualTo("submitted");
        assertThat(response.getLoans().get(0).getPoliceNumber()).isEqualTo("B 1234 BYE");
    }

    @Test
    void requestLoan_savesLoanAndReturnsUserExist() {
        RequestLoanRequest request = buildRequest();
        Loan savedLoan = buildSubmittedLoan();

        when(loanRepository.findByUserIdAndPoliceNumber("Bruce", "B 1234 BYE")).thenReturn(Optional.of(savedLoan));

        assertThatThrownBy(() -> loanService.requestLoan(request))
                .isInstanceOf(InvalidLoanStateException.class)
                .hasMessageContaining("A loan with police number B 1234 BYE already exists for the user.");

        verify(loanRepository, never()).save(any());
    }

    @Test
    void approveLoan_transitionsSubmittedLoanToApproved() {
        ApproveLoanRequest request = new ApproveLoanRequest();
        request.setUserId("Bruce");
        request.setPoliceNumber("B 1234 BYE");

        Loan existingLoan = buildSubmittedLoan();
        when(loanRepository.findByUserIdAndPoliceNumber("Bruce", "B 1234 BYE"))
                .thenReturn(Optional.of(existingLoan));

        ApproveLoanResponse response = loanService.approveLoan(request);

        assertThat(response.getUserId()).isEqualTo("Bruce");
        assertThat(response.getPoliceNumber()).isEqualTo("B 1234 BYE");
        assertThat(response.getMessage()).isEqualTo("Loan updated successfully.");
        assertThat(existingLoan.getStatus()).isEqualTo(LoanStatus.APPROVED);

        verify(loanRepository, times(1)).save(existingLoan);
    }

    @Test
    void approveLoan_throwsLoanNotFoundException_whenLoanDoesNotExist() {
        ApproveLoanRequest request = new ApproveLoanRequest();
        request.setUserId("Bruce");
        request.setPoliceNumber("UNKNOWN");

        when(loanRepository.findByUserIdAndPoliceNumber("Bruce", "UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.approveLoan(request))
                .isInstanceOf(LoanNotFoundException.class)
                .hasMessage("Loan not Found");

        verify(loanRepository, never()).save(any());
    }

    @Test
    void approveLoan_throwsInvalidLoanStateException_whenLoanAlreadyApproved() {
        ApproveLoanRequest request = new ApproveLoanRequest();
        request.setUserId("Bruce");
        request.setPoliceNumber("B 1234 BYE");

        Loan alreadyApproved = buildSubmittedLoan();
        alreadyApproved.setStatus(LoanStatus.APPROVED);

        when(loanRepository.findByUserIdAndPoliceNumber(anyString(), anyString()))
                .thenReturn(Optional.of(alreadyApproved));

        assertThatThrownBy(() -> loanService.approveLoan(request))
                .isInstanceOf(InvalidLoanStateException.class);

        verify(loanRepository, never()).save(any());
    }
}

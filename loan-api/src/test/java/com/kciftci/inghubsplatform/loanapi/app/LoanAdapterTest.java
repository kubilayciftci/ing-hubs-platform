package com.kciftci.inghubsplatform.loanapi.app;

import com.kciftci.inghubsplatform.loanapi.app.entity.Customer;
import com.kciftci.inghubsplatform.loanapi.app.entity.Loan;
import com.kciftci.inghubsplatform.loanapi.app.entity.LoanInstallment;
import com.kciftci.inghubsplatform.loanapi.app.exception.CustomerNotFoundException;
import com.kciftci.inghubsplatform.loanapi.app.model.PayLoan;
import com.kciftci.inghubsplatform.loanapi.app.repository.CustomerRepository;
import com.kciftci.inghubsplatform.loanapi.app.repository.LoanInstallmentRepository;
import com.kciftci.inghubsplatform.loanapi.app.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanAdapterTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @InjectMocks
    private LoanAdapter loanAdapter;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .id(1L)
                .name("Kubilay")
                .surname("Çiftci")
                .creditLimit(new BigDecimal("10000"))
                .usedCreditLimit(BigDecimal.ZERO)
                .build();
    }

    @Test
    void createLoan_WithValidCustomer_ShouldCreateLoan() {
        Long customerId = 1L;
        BigDecimal amount = new BigDecimal("1000");
        BigDecimal interest = new BigDecimal("0.2");
        int numberOfInstallments = 12;

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(testCustomer));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        Loan result = loanAdapter.createLoan(customerId, amount, interest, numberOfInstallments);

        assertThat(result).isNotNull();
        assertThat(result.getCustomer()).isEqualTo(testCustomer);
        assertThat(result.getLoanAmount()).isEqualTo(amount);
        assertThat(result.getInterest()).isEqualTo(interest);
        assertThat(result.getNumberOfInstallment()).isEqualTo(numberOfInstallments);
        assertThat(result.isPaid()).isFalse();
    }

    @Test
    void createLoan_WithNonExistentCustomer_ShouldThrowException() {
        Long customerId = 999L;
        BigDecimal amount = new BigDecimal("1000");
        BigDecimal interest = new BigDecimal("0.2");
        int numberOfInstallments = 12;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanAdapter.createLoan(customerId, amount, interest, numberOfInstallments))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Customer not found with this customerId: 999");
    }

    @Test
    void createLoan_WithInsufficientCreditLimit_ShouldThrowException() {
        Long customerId = 1L;
        BigDecimal amount = new BigDecimal("10000");
        BigDecimal interest = new BigDecimal("0.2");
        int numberOfInstallments = 12;

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(testCustomer));

        assertThatThrownBy(() -> loanAdapter.createLoan(customerId, amount, interest, numberOfInstallments))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Customer does not have enough credit limit");
    }

    @Test
    void createLoan_InstallmentsCorrectness() {
        Long customerId = 1L;
        BigDecimal amount = new BigDecimal("1200");
        BigDecimal interest = new BigDecimal("0.2");
        int numberOfInstallments = 12;

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(testCustomer));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanInstallmentRepository.saveAll(any(List.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        Loan result = loanAdapter.createLoan(customerId, amount, interest, numberOfInstallments);

        assertThat(result).isNotNull();
        verify(loanInstallmentRepository).saveAll(any(List.class));
    }

    @Test
    void payLoan_SuccessfulPayment() {
        Long loanId = 1L;
        BigDecimal paymentAmount = new BigDecimal("400");
        ZonedDateTime paymentDate = ZonedDateTime.now();

        Customer customer = Customer.builder()
                .id(1L)
                .name("Test")
                .surname("User")
                .creditLimit(new BigDecimal("10000"))
                .usedCreditLimit(new BigDecimal("2400"))
                .build();

        Loan loan = Loan.builder()
                .id(loanId)
                .customer(customer)
                .loanAmount(new BigDecimal("2000"))
                .interest(new BigDecimal("0.2"))
                .numberOfInstallment(12)
                .createdAt(ZonedDateTime.now())
                .isPaid(false)
                .build();

        List<LoanInstallment> installments = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            LoanInstallment installment = LoanInstallment.builder()
                    .id((long) (100 + i))
                    .loan(loan)
                    .amount(new BigDecimal("200"))
                    .paidAmount(BigDecimal.ZERO)
                    .dueDate(paymentDate.plusMonths(i + 1))
                    .isPaid(false)
                    .build();
            installments.add(installment);
        }

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findUnpaidInstallmentsByLoanIdAndMaxDueDate(any(), any()))
                .thenReturn(installments.subList(0, 2));
        when(loanInstallmentRepository.findByLoanIdOrderByDueDateAsc(loanId)).thenReturn(installments);
        when(loanInstallmentRepository.save(any(LoanInstallment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PayLoan result = loanAdapter.payLoan(loanId, paymentAmount, paymentDate);

        assertThat(result).isNotNull();
        assertThat(result.getLoanId()).isEqualTo(loanId);
        assertThat(result.getInstallmentsPaid()).isEqualTo(2);
        assertThat(result.isLoanFullyPaid()).isFalse();
    }

    @Test
    void payLoan_InsufficientAmount_ShouldThrowException() {
        Long loanId = 1L;
        BigDecimal paymentAmount = new BigDecimal("100");
        ZonedDateTime paymentDate = ZonedDateTime.now();

        Customer customer = Customer.builder()
                .id(1L)
                .name("Test")
                .surname("User")
                .creditLimit(new BigDecimal("10000"))
                .usedCreditLimit(new BigDecimal("2400"))
                .build();

        Loan loan = Loan.builder()
                .id(loanId)
                .customer(customer)
                .loanAmount(new BigDecimal("2400"))
                .interest(new BigDecimal("0.2"))
                .numberOfInstallment(12)
                .createdAt(ZonedDateTime.now())
                .isPaid(false)
                .build();

        List<LoanInstallment> installments = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            LoanInstallment installment = LoanInstallment.builder()
                    .id((long) (200 + i))
                    .loan(loan)
                    .amount(new BigDecimal("200"))
                    .paidAmount(BigDecimal.ZERO)
                    .dueDate(paymentDate.plusMonths(i + 1))
                    .isPaid(false)
                    .build();
            installments.add(installment);
        }

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findUnpaidInstallmentsByLoanIdAndMaxDueDate(any(), any()))
                .thenReturn(installments.subList(0, 1));

        assertThatThrownBy(() -> loanAdapter.payLoan(loanId, paymentAmount, paymentDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount is not enough to pay any installment");
    }

    @Test
    void payLoan_CannotPayBeyond3Months() {
        Long loanId = 1L;
        BigDecimal paymentAmount = new BigDecimal("200");
        ZonedDateTime paymentDate = ZonedDateTime.now();

        Customer customer = Customer.builder()
                .id(1L)
                .name("Test")
                .surname("User")
                .creditLimit(new BigDecimal("10000"))
                .usedCreditLimit(new BigDecimal("2400"))
                .build();

        Loan loan = Loan.builder()
                .id(loanId)
                .customer(customer)
                .loanAmount(new BigDecimal("2400"))
                .interest(new BigDecimal("0.2"))
                .numberOfInstallment(12)
                .createdAt(ZonedDateTime.now())
                .isPaid(false)
                .build();

        List<LoanInstallment> installments = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            LoanInstallment installment = LoanInstallment.builder()
                    .id((long) (300 + i))
                    .loan(loan)
                    .amount(new BigDecimal("200"))
                    .paidAmount(BigDecimal.ZERO)
                    .dueDate(paymentDate.plusMonths(i + 4))
                    .isPaid(false)
                    .build();
            installments.add(installment);
        }

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findUnpaidInstallmentsByLoanIdAndMaxDueDate(any(), any()))
                .thenReturn(new ArrayList<>());

        assertThatThrownBy(() -> loanAdapter.payLoan(loanId, paymentAmount, paymentDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No payable installments found");
    }

    @Test
    void payLoan_LoanFullyPaidFlag() {
        Long loanId = 1L;
        BigDecimal paymentAmount = new BigDecimal("200");
        ZonedDateTime paymentDate = ZonedDateTime.now();

        Customer customer = Customer.builder()
                .id(1L)
                .name("Test")
                .surname("User")
                .creditLimit(new BigDecimal("10000"))
                .usedCreditLimit(new BigDecimal("200"))
                .build();

        Loan loan = Loan.builder()
                .id(loanId)
                .customer(customer)
                .loanAmount(new BigDecimal("200"))
                .interest(new BigDecimal("0.2"))
                .numberOfInstallment(1)
                .createdAt(ZonedDateTime.now())
                .isPaid(false)
                .build();

        List<LoanInstallment> installments = new ArrayList<>();
        LoanInstallment installment = LoanInstallment.builder()
                .id(400L)
                .loan(loan)
                .amount(new BigDecimal("200"))
                .paidAmount(BigDecimal.ZERO)
                .dueDate(paymentDate.plusMonths(1))
                .isPaid(false)
                .build();
        installments.add(installment);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findUnpaidInstallmentsByLoanIdAndMaxDueDate(any(), any()))
                .thenReturn(installments);
        when(loanInstallmentRepository.findByLoanIdOrderByDueDateAsc(loanId)).thenReturn(installments);
        when(loanInstallmentRepository.save(any(LoanInstallment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PayLoan result = loanAdapter.payLoan(loanId, paymentAmount, paymentDate);

        assertThat(result).isNotNull();
        assertThat(result.isLoanFullyPaid()).isTrue();
        assertThat(result.getInstallmentsPaid()).isEqualTo(1);
    }

    @Test
    void payLoan_BonusAndPenaltyCalculation() {
        Long loanId = 1L;
        BigDecimal paymentAmount = new BigDecimal("200");
        ZonedDateTime paymentDate = ZonedDateTime.now();

        Customer customer = Customer.builder()
                .id(1L)
                .name("Test")
                .surname("User")
                .creditLimit(new BigDecimal("10000"))
                .usedCreditLimit(new BigDecimal("200"))
                .build();

        Loan loan = Loan.builder()
                .id(loanId)
                .customer(customer)
                .loanAmount(new BigDecimal("200"))
                .interest(new BigDecimal("0.2"))
                .numberOfInstallment(1)
                .createdAt(ZonedDateTime.now())
                .isPaid(false)
                .build();

        List<LoanInstallment> earlyInstallments = new ArrayList<>();
        LoanInstallment earlyInstallment = LoanInstallment.builder()
                .id(500L)
                .loan(loan)
                .amount(new BigDecimal("200"))
                .paidAmount(BigDecimal.ZERO)
                .dueDate(paymentDate.plusDays(10))
                .isPaid(false)
                .build();
        earlyInstallments.add(earlyInstallment);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findUnpaidInstallmentsByLoanIdAndMaxDueDate(any(), any()))
                .thenReturn(earlyInstallments);
        when(loanInstallmentRepository.findByLoanIdOrderByDueDateAsc(loanId)).thenReturn(earlyInstallments);
        when(loanInstallmentRepository.save(any(LoanInstallment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PayLoan earlyResult = loanAdapter.payLoan(loanId, paymentAmount, paymentDate);

        assertThat(earlyResult).isNotNull();
        assertThat(earlyResult.getTotalAmountSpent()).isLessThan(new BigDecimal("200"));
        assertThat(earlyResult.getTotalAmountSpent()).isEqualByComparingTo(new BigDecimal("198.00"));

        List<LoanInstallment> lateInstallments = new ArrayList<>();
        LoanInstallment lateInstallment = LoanInstallment.builder()
                .id(501L)
                .loan(loan)
                .amount(new BigDecimal("200"))
                .paidAmount(BigDecimal.ZERO)
                .dueDate(paymentDate.minusDays(5))
                .isPaid(false)
                .build();
        lateInstallments.add(lateInstallment);

        when(loanInstallmentRepository.findUnpaidInstallmentsByLoanIdAndMaxDueDate(any(), any()))
                .thenReturn(lateInstallments);
        when(loanInstallmentRepository.findByLoanIdOrderByDueDateAsc(loanId)).thenReturn(lateInstallments);

        PayLoan lateResult = loanAdapter.payLoan(loanId, paymentAmount, paymentDate);

        assertThat(lateResult).isNotNull();
        assertThat(lateResult.getTotalAmountSpent()).isGreaterThan(new BigDecimal("200"));
        assertThat(lateResult.getTotalAmountSpent()).isEqualByComparingTo(new BigDecimal("201.00"));
    }

    @Test
    void listLoans_ShouldReturnFilteredLoans() {
        Long customerId = 1L;
        Integer numberOfInstallments = 12;
        Boolean isPaid = false;

        List<Loan> expectedLoans = new ArrayList<>();
        expectedLoans.add(Loan.builder()
                .id(1L)
                .customer(testCustomer)
                .loanAmount(new BigDecimal("1000"))
                .interest(new BigDecimal("0.2"))
                .numberOfInstallment(12)
                .createdAt(ZonedDateTime.now())
                .isPaid(false)
                .build());

        when(loanRepository.findByCustomerIdAndFilters(customerId, numberOfInstallments, isPaid))
                .thenReturn(expectedLoans);

        List<Loan> result = loanAdapter.listLoans(customerId, numberOfInstallments, isPaid);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNumberOfInstallment()).isEqualTo(12);
        assertThat(result.get(0).isPaid()).isFalse();
    }

    @Test
    void listLoanInstallments_ShouldReturnInstallments() {
        Long loanId = 1L;
        
        Loan loan = Loan.builder()
                .id(loanId)
                .customer(testCustomer)
                .loanAmount(new BigDecimal("1000"))
                .interest(new BigDecimal("0.2"))
                .numberOfInstallment(12)
                .createdAt(ZonedDateTime.now())
                .isPaid(false)
                .build();
        
        List<LoanInstallment> expectedInstallments = new ArrayList<>();
        expectedInstallments.add(LoanInstallment.builder()
                .id(1L)
                .loan(loan)
                .amount(new BigDecimal("200"))
                .paidAmount(BigDecimal.ZERO)
                .dueDate(ZonedDateTime.now().plusMonths(1))
                .isPaid(false)
                .build());

        when(loanInstallmentRepository.findByLoanIdOrderByDueDateAsc(loanId))
                .thenReturn(expectedInstallments);

        List<LoanInstallment> result = loanAdapter.listLoanInstallments(loanId);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isEqualTo(new BigDecimal("200"));
    }
} 
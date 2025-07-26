package com.kciftci.inghubsplatform.loanapi.app;

import com.kciftci.inghubsplatform.loanapi.app.entity.Customer;
import com.kciftci.inghubsplatform.loanapi.app.entity.Loan;
import com.kciftci.inghubsplatform.loanapi.app.entity.LoanInstallment;
import com.kciftci.inghubsplatform.loanapi.app.exception.CustomerNotFoundException;
import com.kciftci.inghubsplatform.loanapi.app.model.PayLoan;
import com.kciftci.inghubsplatform.loanapi.app.repository.CustomerRepository;
import com.kciftci.inghubsplatform.loanapi.app.repository.LoanInstallmentRepository;
import com.kciftci.inghubsplatform.loanapi.app.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanAdapter implements LoanPort {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;

    @Override
    @Transactional
    public Loan createLoan(Long customerId, BigDecimal amount, BigDecimal interest, int numberOfInstallments) {

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with this customerId: " + customerId));

        BigDecimal totalLoanAmount = amount.multiply(BigDecimal.ONE.add(interest));
        BigDecimal availableCredit = customer.getCreditLimit().subtract(customer.getUsedCreditLimit());

        if (availableCredit.compareTo(totalLoanAmount) < 0) {
            throw new IllegalArgumentException("Customer does not have enough credit limit");
        }

        Loan loan = Loan.builder()
            .customer(customer)
            .loanAmount(amount)
            .interest(interest)
            .numberOfInstallment(numberOfInstallments)
            .createdAt(ZonedDateTime.now())
            .isPaid(false)
            .build();

        loan = loanRepository.save(loan);

        BigDecimal installmentAmount = totalLoanAmount.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);
        List<LoanInstallment> installments = new ArrayList<>();

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime firstDueDate = now.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());

        for (int i = 0; i < numberOfInstallments; i++) {
            ZonedDateTime dueDate = firstDueDate.plusMonths(i);

            LoanInstallment installment = LoanInstallment.builder()
                .loan(loan)
                .amount(installmentAmount)
                .paidAmount(BigDecimal.ZERO)
                .dueDate(dueDate)
                .paymentDate(null)
                .isPaid(false)
                .build();

            installments.add(installment);
        }

        loanInstallmentRepository.saveAll(installments);

        customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(totalLoanAmount));
        customerRepository.save(customer);

        return loan;
    }

    @Override
    public List<Loan> listLoans(Long customerId, Integer numberOfInstallments, Boolean isPaid) {
        return loanRepository.findByCustomerIdAndFilters(customerId, numberOfInstallments, isPaid);
    }

    @Override
    public List<LoanInstallment> listLoanInstallments(Long loanId) {
        return loanInstallmentRepository.findByLoanIdOrderByDueDateAsc(loanId);
    }

    @Override
    @Transactional
    public PayLoan payLoan(Long loanId, BigDecimal amount, ZonedDateTime paymentDate) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanId));

        ZonedDateTime maxDueDate = paymentDate.plusMonths(3);
        List<LoanInstallment> unpaidInstallments = loanInstallmentRepository
            .findUnpaidInstallmentsByLoanIdAndMaxDueDate(loanId, maxDueDate);

        if (unpaidInstallments.isEmpty()) {
            throw new IllegalArgumentException("No payable installments found");
        }

        BigDecimal installmentAmount = unpaidInstallments.get(0).getAmount();
        int maxPayable = amount.divideToIntegralValue(installmentAmount).intValue();
        if (maxPayable == 0) {
            throw new IllegalArgumentException("Amount is not enough to pay any installment");
        }

        int toPay = Math.min(maxPayable, unpaidInstallments.size());
        BigDecimal totalAmountSpent = BigDecimal.ZERO;

        for (int i = 0; i < toPay; i++) {
            LoanInstallment installment = unpaidInstallments.get(i);
            BigDecimal finalAmount = calculateFinalAmount(installment, paymentDate);

            installment.setPaidAmount(finalAmount);
            installment.setPaymentDate(paymentDate);
            installment.setPaid(true);

            totalAmountSpent = totalAmountSpent.add(finalAmount);
            loanInstallmentRepository.save(installment);
        }

        boolean loanFullyPaid = loanInstallmentRepository.findByLoanIdOrderByDueDateAsc(loanId)
            .stream()
            .allMatch(LoanInstallment::isPaid);

        if (loanFullyPaid) {
            loan.setPaid(true);
            loanRepository.save(loan);

            Customer customer = loan.getCustomer();
            BigDecimal totalLoanAmount = loan.getLoanAmount().multiply(BigDecimal.ONE.add(loan.getInterest()));
            customer.setUsedCreditLimit(customer.getUsedCreditLimit().subtract(totalLoanAmount));
            customerRepository.save(customer);
        }

        return PayLoan.builder()
            .loanId(loanId)
            .installmentsPaid(toPay)
            .totalAmountSpent(totalAmountSpent)
            .loanFullyPaid(loanFullyPaid)
            .build();
    }

    private BigDecimal calculateFinalAmount(LoanInstallment installment, ZonedDateTime paymentDate) {
        BigDecimal baseAmount = installment.getAmount();

        if (paymentDate.isBefore(installment.getDueDate())) {
            long daysBeforeDue = java.time.Duration.between(paymentDate, installment.getDueDate()).toDays();
            BigDecimal discount = baseAmount.multiply(BigDecimal.valueOf(0.001)).multiply(BigDecimal.valueOf(daysBeforeDue));
            return baseAmount.subtract(discount);
        } else if (paymentDate.isAfter(installment.getDueDate())) {
            long daysAfterDue = java.time.Duration.between(installment.getDueDate(), paymentDate).toDays();
            BigDecimal penalty = baseAmount.multiply(BigDecimal.valueOf(0.001)).multiply(BigDecimal.valueOf(daysAfterDue));
            return baseAmount.add(penalty);
        } else {
            return baseAmount;
        }
    }
}

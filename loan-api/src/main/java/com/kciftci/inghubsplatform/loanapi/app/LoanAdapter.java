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

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanAdapter implements LoanPort {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;

    @Override
    public Loan createLoan(Long customerId, BigDecimal amount, BigDecimal interest, int numberOfInstallments) {

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(
                "Customer not found with this customerId: " + customerId));

        BigDecimal totalLoanAmount = amount.multiply(BigDecimal.ONE.add(interest));
        if (customer.getCreditLimit().subtract(customer.getUsedCreditLimit()).compareTo(totalLoanAmount) < 0) {
            throw new IllegalArgumentException("Customer does not have enough credit limit");
        }
        return null;
    }

    @Override
    public List<Loan> listLoans(Long customerId, int numberOfInstallments, boolean isPaid) {
        return List.of();
    }

    @Override
    public List<LoanInstallment> listLoanInstallments(Long loanId) {
        return List.of();
    }

    @Override
    public PayLoan payLoan(Long loanId, Double amount, ZonedDateTime paymentDate) {
        return null;
    }
}

package com.kciftci.inghubsplatform.loanapi.app;

import com.kciftci.inghubsplatform.loanapi.app.entity.Loan;
import com.kciftci.inghubsplatform.loanapi.app.entity.LoanInstallment;
import com.kciftci.inghubsplatform.loanapi.app.model.PayLoan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanFacade {

    private final LoanPort loanPort;

    public Loan createLoan(Long customerId, BigDecimal amount, BigDecimal interest, int numberOfInstallments) {
        return loanPort.createLoan(customerId, amount, interest, numberOfInstallments);
    }

    public List<Loan> listLoans(Long customerId, Integer numberOfInstallments, Boolean isPaid) {
        return loanPort.listLoans(customerId, numberOfInstallments, isPaid);
    }

    public List<LoanInstallment> listLoanInstallments(Long loanId) {
        return loanPort.listLoanInstallments(loanId);
    }

    public PayLoan payLoan(Long loanId, BigDecimal amount, ZonedDateTime paymentDate) {
        return loanPort.payLoan(loanId, amount, paymentDate);
    }
}

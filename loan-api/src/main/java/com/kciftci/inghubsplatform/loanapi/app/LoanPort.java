package com.kciftci.inghubsplatform.loanapi.app;

import com.kciftci.inghubsplatform.loanapi.app.entity.Loan;
import com.kciftci.inghubsplatform.loanapi.app.entity.LoanInstallment;
import com.kciftci.inghubsplatform.loanapi.app.model.PayLoan;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public interface LoanPort {

    Loan createLoan(Long customerId, BigDecimal amount, BigDecimal interest, int numberOfInstallments);

    List<Loan> listLoans(Long customerId, Integer numberOfInstallments, Boolean isPaid);

    List<LoanInstallment> listLoanInstallments(Long loanId);

    PayLoan payLoan(Long loanId, BigDecimal amount, ZonedDateTime paymentDate);
}

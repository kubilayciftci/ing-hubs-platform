package com.kciftci.inghubsplatform.loanapi.app.rest;

import com.kciftci.inghubsplatform.loanapi.app.LoanFacade;
import com.kciftci.inghubsplatform.loanapi.app.entity.Loan;
import com.kciftci.inghubsplatform.loanapi.app.entity.LoanInstallment;
import com.kciftci.inghubsplatform.loanapi.app.model.PayLoan;
import com.kciftci.inghubsplatform.loanapi.app.rest.dto.LoanInstallmentResponse;
import com.kciftci.inghubsplatform.loanapi.app.rest.dto.LoanRequest;
import com.kciftci.inghubsplatform.loanapi.app.rest.dto.LoanResponse;
import com.kciftci.inghubsplatform.loanapi.app.rest.dto.PayLoanRequest;
import com.kciftci.inghubsplatform.loanapi.app.rest.dto.PayLoanResponse;
import com.kciftci.inghubsplatform.loanapi.app.rest.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/loan")
public class LoanController {

    private final LoanFacade loanFacade;
    private final RequestValidator requestValidator;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public LoanResponse createLoan(@RequestBody LoanRequest loanRequest) {
        requestValidator.validateInterestRate(loanRequest.getInterest());
        requestValidator.validateInstallments(loanRequest.getNumberOfInstallments());

        Loan loan = loanFacade.createLoan(loanRequest.getCustomerId(), loanRequest.getAmount(),
            loanRequest.getInterest(), loanRequest.getNumberOfInstallments());
        return LoanResponse.of(loan);
    }

    @GetMapping
    public List<LoanResponse> listLoans(@RequestParam Long customerId,
                                        @RequestParam(required = false) Integer numberOfInstallments,
                                        @RequestParam(required = false) Boolean isPaid) {

        List<Loan> loans = loanFacade.listLoans(customerId, numberOfInstallments, isPaid);
        return LoanResponse.listOf(loans);
    }

    @GetMapping("/installments/{loanId}")
    public List<LoanInstallmentResponse> listInstallments(@PathVariable Long loanId) {
        List<LoanInstallment> installments = loanFacade.listLoanInstallments(loanId);
        return LoanInstallmentResponse.listOf(installments);
    }

    @PostMapping("/pay/{loanId}")
    public PayLoanResponse payLoan(@PathVariable Long loanId, @RequestBody PayLoanRequest payLoanRequest) {
        PayLoan paidLoan = loanFacade.payLoan(loanId, payLoanRequest.getAmount(), payLoanRequest.getPaymentDate());
        return PayLoanResponse.of(paidLoan);
    }


}

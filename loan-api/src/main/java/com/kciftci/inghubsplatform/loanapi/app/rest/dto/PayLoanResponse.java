package com.kciftci.inghubsplatform.loanapi.app.rest.dto;

import com.kciftci.inghubsplatform.loanapi.app.model.PayLoan;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PayLoanResponse {
    private Long loanId;
    private int installmentsPaid;
    private BigDecimal totalAmountSpent;
    private boolean loanFullyPaid;

    public static PayLoanResponse of(PayLoan payLoan) {
        return PayLoanResponse.builder()
                .loanId(payLoan.getLoanId())
                .installmentsPaid(payLoan.getInstallmentsPaid())
                .totalAmountSpent(payLoan.getTotalAmountSpent())
                .loanFullyPaid(payLoan.isLoanFullyPaid())
                .build();
    }
}

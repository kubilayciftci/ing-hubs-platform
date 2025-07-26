package com.kciftci.inghubsplatform.loanapi.app.rest.dto;

import com.kciftci.inghubsplatform.loanapi.app.model.PayLoan;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayLoanResponse {

    private int paidInstallmentCount;
    private Double totalPaidAmount;
    private boolean loanFullyPaid;

    private static PayLoanResponse from(final PayLoan payLoan) {
        return PayLoanResponse.builder()
            .paidInstallmentCount(payLoan.getPaidInstallmentCount())
            .totalPaidAmount(payLoan.getTotalPaidAmount())
            .loanFullyPaid(payLoan.isLoanFullyPaid())
            .build();
    }


    public static PayLoanResponse of(final PayLoan payLoan) {
        return from(payLoan);
    }
}

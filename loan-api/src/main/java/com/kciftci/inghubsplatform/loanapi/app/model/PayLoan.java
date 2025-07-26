package com.kciftci.inghubsplatform.loanapi.app.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayLoan {
    private int paidInstallmentCount;
    private Double totalPaidAmount;
    private boolean loanFullyPaid;
}

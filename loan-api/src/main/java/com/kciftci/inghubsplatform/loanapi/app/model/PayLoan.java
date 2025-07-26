package com.kciftci.inghubsplatform.loanapi.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayLoan {
    private Long loanId;
    private int installmentsPaid;
    private BigDecimal totalAmountSpent;
    private boolean loanFullyPaid;
}

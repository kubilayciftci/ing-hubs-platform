package com.kciftci.inghubsplatform.loanapi.app.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LoanRequest {
    private Long customerId;
    private BigDecimal amount;
    private BigDecimal interest;
    private int numberOfInstallments;
}

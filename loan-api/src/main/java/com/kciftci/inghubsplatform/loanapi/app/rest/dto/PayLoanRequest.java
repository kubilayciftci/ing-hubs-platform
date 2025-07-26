package com.kciftci.inghubsplatform.loanapi.app.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
public class PayLoanRequest {
    private BigDecimal amount;
    private ZonedDateTime paymentDate;
}

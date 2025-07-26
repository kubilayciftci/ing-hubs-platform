package com.kciftci.inghubsplatform.loanapi.app.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class PayLoanRequest {
    private Double amount;
    private ZonedDateTime paymentDate;
}

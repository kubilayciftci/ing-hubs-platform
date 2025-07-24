package com.kciftci.inghubsplatform.loanapi.app.model;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class Loan {
    private Long id;
    private Customer customer;
    private BigDecimal loanAmount;
    private Integer numberOfInstallment;
    private ZonedDateTime createdAt;
    private boolean isPaid;
    private List<LoanInstallment> installments;
}

package com.kciftci.inghubsplatform.loanapi.app.rest.dto;

import com.kciftci.inghubsplatform.loanapi.app.entity.Customer;
import com.kciftci.inghubsplatform.loanapi.app.entity.Loan;
import com.kciftci.inghubsplatform.loanapi.app.entity.LoanInstallment;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class LoanResponse {

    private Long id;
    private Customer customer;
    private BigDecimal loanAmount;
    private Integer numberOfInstallment;
    private ZonedDateTime createdAt;
    private boolean isPaid;
    private List<LoanInstallment> installments;


    private static LoanResponse from(final Loan loan) {
        return LoanResponse.builder()
            .id(loan.getId())
            .customer(loan.getCustomer())
            .loanAmount(loan.getLoanAmount())
            .numberOfInstallment(loan.getNumberOfInstallment())
            .createdAt(loan.getCreatedAt())
            .isPaid(loan.isPaid())
            .installments(loan.getInstallments())
            .build();
    }

    public static LoanResponse of(final Loan loan) {
        return from(loan);
    }

    public static List<LoanResponse> listOf(List<Loan> loans) {
        // TODO: complete this method
        return List.of();
    }
}

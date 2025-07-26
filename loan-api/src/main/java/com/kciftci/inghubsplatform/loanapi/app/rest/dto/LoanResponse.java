package com.kciftci.inghubsplatform.loanapi.app.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kciftci.inghubsplatform.loanapi.app.entity.Loan;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanResponse {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Long id;
    private Long customerId;
    private BigDecimal loanAmount;
    private BigDecimal interest;
    private Integer numberOfInstallment;
    private String createdAt;
    private boolean isPaid;

    public static LoanResponse of(Loan loan) {
        LoanResponseBuilder builder = LoanResponse.builder()
            .id(loan.getId())
            .customerId(loan.getCustomer().getId())
            .loanAmount(loan.getLoanAmount())
            .interest(loan.getInterest())
            .numberOfInstallment(loan.getNumberOfInstallment())
            .isPaid(loan.isPaid());

        if (loan.getCreatedAt() != null) {
            builder.createdAt(loan.getCreatedAt().format(DATE_FORMATTER));
        }

        return builder.build();
    }

    public static List<LoanResponse> listOf(List<Loan> loans) {
        return loans.stream()
            .map(LoanResponse::of)
            .collect(Collectors.toList());
    }
}

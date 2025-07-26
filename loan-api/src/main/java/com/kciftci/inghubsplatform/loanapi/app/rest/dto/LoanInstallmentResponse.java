package com.kciftci.inghubsplatform.loanapi.app.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kciftci.inghubsplatform.loanapi.app.entity.LoanInstallment;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanInstallmentResponse {
    private Long id;
    private Long loanId;
    private Long customerId;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private String dueDate;
    private String paymentDate;
    private boolean isPaid;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LoanInstallmentResponse of(LoanInstallment installment) {
        LoanInstallmentResponseBuilder builder = LoanInstallmentResponse.builder()
                .id(installment.getId())
                .loanId(installment.getLoan().getId())
                .customerId(installment.getLoan().getCustomer().getId())
                .amount(installment.getAmount())
                .paidAmount(installment.getPaidAmount())
                .dueDate(installment.getDueDate() != null ? installment.getDueDate().format(DATE_FORMATTER) : null)
                .isPaid(installment.isPaid());

        if (installment.getPaymentDate() != null) {
            builder.paymentDate(installment.getPaymentDate().format(DATE_FORMATTER));
        }

        return builder.build();
    }

    public static List<LoanInstallmentResponse> listOf(List<LoanInstallment> installments) {
        return installments.stream()
                .map(LoanInstallmentResponse::of)
                .collect(Collectors.toList());
    }
}

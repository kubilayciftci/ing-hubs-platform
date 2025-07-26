package com.kciftci.inghubsplatform.loanapi.app.rest.dto;

import com.kciftci.inghubsplatform.loanapi.app.entity.LoanInstallment;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class LoanInstallmentResponse {

    private Long id;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private boolean isPaid;

    public static List<LoanInstallmentResponse> listOf(List<LoanInstallment> installments) {
        // TODO: complete this method
        return List.of();
    }
}

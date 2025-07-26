package com.kciftci.inghubsplatform.loanapi.app.rest.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class RequestValidator {

    public void validateInterestRate(BigDecimal interest) {
        if (interest.compareTo(BigDecimal.valueOf(0.1)) < 0 || interest.compareTo(BigDecimal.valueOf(0.5)) > 0) {
            throw new IllegalArgumentException("Interest rate must be between 0.1 and 0.5");
        }
    }

    public void validateInstallments(int numberOfInstallments) {
        if (!(numberOfInstallments == 6 || numberOfInstallments == 9 || numberOfInstallments == 12 || numberOfInstallments == 24)) {
            throw new IllegalArgumentException("Number of installments must be 6, 9, 12 or 24");
        }
    }

}

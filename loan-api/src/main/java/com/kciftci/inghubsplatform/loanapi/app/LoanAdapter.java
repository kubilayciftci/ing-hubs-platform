package com.kciftci.inghubsplatform.loanapi.app;

import com.kciftci.inghubsplatform.loanapi.app.repository.CustomerRepository;
import com.kciftci.inghubsplatform.loanapi.app.repository.LoanInstallmentRepository;
import com.kciftci.inghubsplatform.loanapi.app.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanAdapter implements LoanPort {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;

}

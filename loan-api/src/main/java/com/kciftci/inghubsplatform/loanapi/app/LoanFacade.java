package com.kciftci.inghubsplatform.loanapi.app;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanFacade {

    private final LoanPort loanPort;
}

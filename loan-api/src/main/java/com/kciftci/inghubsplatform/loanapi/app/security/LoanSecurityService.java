package com.kciftci.inghubsplatform.loanapi.app.security;

import com.kciftci.inghubsplatform.loanapi.app.entity.Loan;
import com.kciftci.inghubsplatform.loanapi.app.entity.User;
import com.kciftci.inghubsplatform.loanapi.app.exception.UnauthorizedAccessException;
import com.kciftci.inghubsplatform.loanapi.app.model.UserRole;
import com.kciftci.inghubsplatform.loanapi.app.repository.LoanRepository;
import com.kciftci.inghubsplatform.loanapi.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanSecurityService {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    public void validateCustomerAccess(Long customerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UnauthorizedAccessException("User not found"));

        if (user.getRole() == UserRole.ADMIN) {
            return;
        }

        if (user.getRole() == UserRole.CUSTOMER) {
            if (user.getCustomer() == null || !user.getCustomer().getId().equals(customerId)) {
                throw new UnauthorizedAccessException("Customer can only access their own data");
            }
        }
    }

    public void validateLoanAccess(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new UnauthorizedAccessException("Loan not found"));

        validateCustomerAccess(loan.getCustomer().getId());
    }

    public Long getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UnauthorizedAccessException("User not found"));

        if (user.getRole() == UserRole.CUSTOMER) {
            if (user.getCustomer() == null) {
                throw new UnauthorizedAccessException("Customer user has no associated customer record");
            }
            return user.getCustomer().getId();
        }

        throw new UnauthorizedAccessException("Only customer users can get their customer ID");
    }
} 
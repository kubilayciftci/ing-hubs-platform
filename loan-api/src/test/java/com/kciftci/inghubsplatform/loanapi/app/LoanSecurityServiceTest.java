package com.kciftci.inghubsplatform.loanapi.app;

import com.kciftci.inghubsplatform.loanapi.app.entity.Customer;
import com.kciftci.inghubsplatform.loanapi.app.entity.User;
import com.kciftci.inghubsplatform.loanapi.app.exception.UnauthorizedAccessException;
import com.kciftci.inghubsplatform.loanapi.app.model.UserRole;
import com.kciftci.inghubsplatform.loanapi.app.repository.LoanRepository;
import com.kciftci.inghubsplatform.loanapi.app.repository.UserRepository;
import com.kciftci.inghubsplatform.loanapi.app.security.LoanSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanSecurityServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private LoanSecurityService loanSecurityService;

    @BeforeEach
    void setUp() {
        loanSecurityService = new LoanSecurityService(userRepository, loanRepository);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void validateCustomerAccess_AdminUser_ShouldAllowAccess() {
        Long customerId = 1L;
        User adminUser = User.builder()
            .id(1L)
            .username("admin")
            .role(UserRole.ADMIN)
            .build();

        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        loanSecurityService.validateCustomerAccess(customerId);
    }

    @Test
    void validateCustomerAccess_CustomerUserWithOwnData_ShouldAllowAccess() {
        Long customerId = 1L;
        Customer customer = Customer.builder().id(customerId).build();
        User customerUser = User.builder()
            .id(2L)
            .username("customer")
            .role(UserRole.CUSTOMER)
            .customer(customer)
            .build();

        when(authentication.getName()).thenReturn("customer");
        when(userRepository.findByUsername("customer")).thenReturn(Optional.of(customerUser));

        loanSecurityService.validateCustomerAccess(customerId);
    }

    @Test
    void validateCustomerAccess_CustomerUserWithOtherData_ShouldThrowException() {
        Long customerId = 2L;
        Customer customer = Customer.builder().id(1L).build();
        User customerUser = User.builder()
            .id(2L)
            .username("customer")
            .role(UserRole.CUSTOMER)
            .customer(customer)
            .build();

        when(authentication.getName()).thenReturn("customer");
        when(userRepository.findByUsername("customer")).thenReturn(Optional.of(customerUser));

        assertThatThrownBy(() -> loanSecurityService.validateCustomerAccess(customerId))
            .isInstanceOf(UnauthorizedAccessException.class)
            .hasMessage("Customer can only access their own data");
    }

    @Test
    void getCurrentCustomerId_CustomerUser_ShouldReturnCustomerId() {
        Long customerId = 1L;
        Customer customer = Customer.builder().id(customerId).build();
        User customerUser = User.builder()
            .id(2L)
            .username("customer")
            .role(UserRole.CUSTOMER)
            .customer(customer)
            .build();

        when(authentication.getName()).thenReturn("customer");
        when(userRepository.findByUsername("customer")).thenReturn(Optional.of(customerUser));

        Long result = loanSecurityService.getCurrentCustomerId();

        assertThat(result).isEqualTo(customerId);
    }

    @Test
    void getCurrentCustomerId_AdminUser_ShouldThrowException() {
        User adminUser = User.builder()
            .id(1L)
            .username("admin")
            .role(UserRole.ADMIN)
            .build();

        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        assertThatThrownBy(() -> loanSecurityService.getCurrentCustomerId())
            .isInstanceOf(UnauthorizedAccessException.class)
            .hasMessage("Only customer users can get their customer ID");
    }
} 
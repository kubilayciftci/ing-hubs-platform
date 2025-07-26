package com.kciftci.inghubsplatform.loanapi.app.repository;

import com.kciftci.inghubsplatform.loanapi.app.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("SELECT l FROM Loan l WHERE l.customer.id = :customerId " +
        "AND (:numberOfInstallments IS NULL OR l.numberOfInstallment = :numberOfInstallments) " +
        "AND (:isPaid IS NULL OR l.isPaid = :isPaid)")
    List<Loan> findByCustomerIdAndFilters(@Param("customerId") Long customerId,
                                          @Param("numberOfInstallments") Integer numberOfInstallments,
                                          @Param("isPaid") Boolean isPaid);
}

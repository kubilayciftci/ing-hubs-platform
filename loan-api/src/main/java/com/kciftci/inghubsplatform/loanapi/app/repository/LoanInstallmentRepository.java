package com.kciftci.inghubsplatform.loanapi.app.repository;

import com.kciftci.inghubsplatform.loanapi.app.entity.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {
    
    List<LoanInstallment> findByLoanIdOrderByDueDateAsc(Long loanId);
    
    @Query("SELECT li FROM LoanInstallment li WHERE li.loan.id = :loanId " +
           "AND li.isPaid = false " +
           "AND li.dueDate <= :maxDueDate " +
           "ORDER BY li.dueDate ASC")
    List<LoanInstallment> findUnpaidInstallmentsByLoanIdAndMaxDueDate(
            @Param("loanId") Long loanId,
            @Param("maxDueDate") ZonedDateTime maxDueDate);
}

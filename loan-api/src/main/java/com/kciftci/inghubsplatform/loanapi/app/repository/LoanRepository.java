package com.kciftci.inghubsplatform.loanapi.app.repository;

import com.kciftci.inghubsplatform.loanapi.app.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
}

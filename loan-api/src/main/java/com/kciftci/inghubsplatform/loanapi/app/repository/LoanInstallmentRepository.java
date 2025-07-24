package com.kciftci.inghubsplatform.loanapi.app.repository;

import com.kciftci.inghubsplatform.loanapi.app.entity.LoanInstallmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallmentEntity, Long> {

}

package com.kciftci.inghubsplatform.loanapi.app.repository;

import com.kciftci.inghubsplatform.loanapi.app.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

}

package com.software.bank_account_manager.query.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.software.bank_account_manager.query.models.CurrentAccountView;

public interface CurrentAccountViewRepository extends JpaRepository<CurrentAccountView, String>{

}

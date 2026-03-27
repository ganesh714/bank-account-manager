package com.software.bank_account_manager.query.projections;

import org.axonframework.config.ProcessingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.software.bank_account_manager.query.repositories.CurrentAccountViewRepository;

@Component
@ProcessingGroup("current-account-view")

public class CurrentAccountViewProjection {
	
	@Autowired
	CurrentAccountViewRepository repository;
	
	
}

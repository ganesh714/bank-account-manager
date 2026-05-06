package com.software.bank_account_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"org.axonframework.eventsourcing.eventstore.jpa", "org.axonframework.eventhandling.tokenstore.jpa", "org.axonframework.modelling.saga.repository.jpa", "com.software.bank_account_manager"})
public class BankAccountManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankAccountManagerApplication.class, args);
	}

}

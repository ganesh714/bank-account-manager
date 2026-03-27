package com.software.bank_account_manager.query.projections;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.software.bank_account_manager.coreapi.events.AccountClosedEvent;
import com.software.bank_account_manager.coreapi.events.AccountCreatedEvent;
import com.software.bank_account_manager.coreapi.events.MoneyDepositedEvent;
import com.software.bank_account_manager.coreapi.events.MoneyWithdrawnEvent;
import com.software.bank_account_manager.query.models.CurrentAccountView;
import com.software.bank_account_manager.query.repositories.CurrentAccountViewRepository;

@Component
@ProcessingGroup("current-account-view")

public class CurrentAccountViewProjection {
	
	@Autowired
	CurrentAccountViewRepository repository;
	
	@EventHandler
    public void on(AccountCreatedEvent event) {
        // When an account is created, save a new row in our view table
        CurrentAccountView view = new CurrentAccountView(
                event.accountId(),
                event.ownerName(),
                event.initialBalance(),
                "ACTIVE"
        );
        repository.save(view);
    }

    @EventHandler
    public void on(MoneyDepositedEvent event) {
        // Find the existing account, update the balance, and save it
        repository.findById(event.accountId()).ifPresent(view -> {
            view.setBalance(view.getBalance().add(event.amount()));
            repository.save(view);
        });
    }

    @EventHandler
    public void on(MoneyWithdrawnEvent event) {
        repository.findById(event.accountId()).ifPresent(view -> {
            view.setBalance(view.getBalance().subtract(event.amount()));
            repository.save(view);
        });
    }

    @EventHandler
    public void on(AccountClosedEvent event) {
        repository.findById(event.accountId()).ifPresent(view -> {
            view.setStatus("CLOSED");
            repository.save(view);
        });
    }
}

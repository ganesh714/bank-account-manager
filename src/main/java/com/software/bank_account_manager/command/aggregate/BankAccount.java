package com.software.bank_account_manager.command.aggregate;

import java.math.BigDecimal;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.software.bank_account_manager.coreapi.commands.CloseAccountCommand;
import com.software.bank_account_manager.coreapi.commands.CreateAccountCommand;
import com.software.bank_account_manager.coreapi.events.AccountClosedEvent;
import com.software.bank_account_manager.coreapi.events.AccountCreatedEvent;
import com.software.bank_account_manager.coreapi.events.MoneyDepositedEvent;
import com.software.bank_account_manager.coreapi.events.MoneyWithdrawnEvent;


@Aggregate
public class BankAccount {
	
	@AggregateIdentifier
	private String accountId;
    private BigDecimal balance;
    private String ownerName;
    private String status;
    
    protected BankAccount() {
    }
    
    @CommandHandler
    public BankAccount(CreateAccountCommand command) {
    	// 1. Validate Business Rules
        if (command.initialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        
        // 2. Apply the Event (This saves it to the DB and triggers the EventSourcingHandler)
        AggregateLifecycle.apply(new AccountCreatedEvent(
        		command.accountId(),
        		command.initialBalance(),
        		command.ownerName()
        ));
    }
    
    @CommandHandler
    public void handle(CloseAccountCommand command) {
    	// Account balance must be exactly zero to close
    	if (this.balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Account balance must be exactly zero to close");
        }
        AggregateLifecycle.apply(new AccountClosedEvent(command.accountId()));
    }
    
    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.accountId();
        this.balance = event.initialBalance();
        this.ownerName = event.ownerName();
        this.status = "ACTIVE";
    }

    @EventSourcingHandler
    public void on(MoneyDepositedEvent event) {
        this.balance = this.balance.add(event.amount());
    }

    @EventSourcingHandler
    public void on(MoneyWithdrawnEvent event) {
        this.balance = this.balance.subtract(event.amount());
    }

    @EventSourcingHandler
    public void on(AccountClosedEvent event) {
        this.status = "CLOSED";
    }
}

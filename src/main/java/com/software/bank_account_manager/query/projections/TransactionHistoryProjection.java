package com.software.bank_account_manager.query.projections;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.software.bank_account_manager.coreapi.events.MoneyDepositedEvent;
import com.software.bank_account_manager.coreapi.events.MoneyWithdrawnEvent;
import com.software.bank_account_manager.query.models.TransactionHistory;
import com.software.bank_account_manager.query.repositories.TransactionHistoryRepository;
import java.time.Instant;

@Component
@ProcessingGroup("transaction-history")
public class TransactionHistoryProjection {

    @Autowired
    TransactionHistoryRepository repository;

    @EventHandler
    public void on(MoneyDepositedEvent event, @Timestamp Instant timestamp) {
        TransactionHistory history = new TransactionHistory(
                event.accountId(),
                "DEPOSIT",
                event.amount(),
                timestamp
        );
        repository.save(history);
    }

    @EventHandler
    public void on(MoneyWithdrawnEvent event, @Timestamp Instant timestamp) {
        TransactionHistory history = new TransactionHistory(
                event.accountId(),
                "WITHDRAWAL",
                event.amount(),
                timestamp
        );
        repository.save(history);
    }
}

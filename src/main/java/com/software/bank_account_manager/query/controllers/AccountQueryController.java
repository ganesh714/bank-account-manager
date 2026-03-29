package com.software.bank_account_manager.query.controllers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.software.bank_account_manager.coreapi.events.AccountCreatedEvent;
import com.software.bank_account_manager.coreapi.events.MoneyDepositedEvent;
import com.software.bank_account_manager.coreapi.events.MoneyWithdrawnEvent;
import com.software.bank_account_manager.query.models.CurrentAccountView;
import com.software.bank_account_manager.query.repositories.CurrentAccountViewRepository;

@RestController
@RequestMapping("/api/accounts")
public class AccountQueryController {
	
	@Autowired
	CurrentAccountViewRepository repository;
	@Autowired
	EventStore eventStore;
	
	@GetMapping("/{accountId}")
	public ResponseEntity<CurrentAccountView> getAccount(@PathVariable String accountId) {
        return repository.findById(accountId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
	
	@GetMapping("/{accountId}/events")
    public ResponseEntity<List<Map<String, Object>>> getAccountEvents(@PathVariable String accountId) {
        try {
            // Fetch all events for this specific aggregate
            DomainEventStream eventStream = eventStore.readEvents(accountId);
            List<Map<String, Object>> events = new ArrayList<>();

            // Format them nicely into a JSON array
            eventStream.asStream().forEach(event -> events.add(Map.of(
                    "type", event.getPayloadType().getSimpleName(),
                    "payload", event.getPayload()
            )));

            return ResponseEntity.ok(events);
        } catch (Exception e) {
            // Axon throws an exception if the stream doesn't exist
            return ResponseEntity.notFound().build();
        }
    }
	
	@GetMapping("/{accountId}/balance-at/{timestamp}")
    public ResponseEntity<Map<String, Object>> getBalanceAt(
            @PathVariable String accountId, 
            @PathVariable String timestamp) {
        try {
            Instant targetTime = Instant.parse(timestamp);
            DomainEventStream eventStream = eventStore.readEvents(accountId);
            
            BigDecimal balance = BigDecimal.ZERO;
            boolean accountExists = false;

            // Replay events one by one, manually calculating the state
            for (DomainEventMessage<?> eventMessage : eventStream.asStream().toList()) {
                
                // Stop replaying if the event happened AFTER our target timestamp
                if (eventMessage.getTimestamp().isAfter(targetTime)) {
                    break; 
                }
                
                Object event = eventMessage.getPayload();
                if (event instanceof AccountCreatedEvent e) {
                    balance = e.initialBalance();
                    accountExists = true;
                } else if (event instanceof MoneyDepositedEvent e) {
                    balance = balance.add(e.amount());
                } else if (event instanceof MoneyWithdrawnEvent e) {
                    balance = balance.subtract(e.amount());
                }
            }

            if (!accountExists) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(Map.of(
                    "accountId", accountId,
                    "balanceAsOf", timestamp,
                    "balance", balance
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Bad timestamp format or missing account
        }
    }
}

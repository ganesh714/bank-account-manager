package com.software.bank_account_manager.command.controllers;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.software.bank_account_manager.coreapi.commands.CloseAccountCommand;
import com.software.bank_account_manager.coreapi.commands.CreateAccountCommand;
import com.software.bank_account_manager.coreapi.commands.DepositMoneyCommand;
import com.software.bank_account_manager.coreapi.commands.WithdrawMoneyCommand;

@RestController
@RequestMapping("/api/accounts")
public class AccountCommandController {
	
	@Autowired
	CommandGateway commandGateway;
	
	@PostMapping
    public ResponseEntity<String> createAccount(@RequestBody CreateAccountRequest request) {
		
		String accountId = UUID.randomUUID().toString();
		
		CreateAccountCommand command = new CreateAccountCommand(
				accountId, 
				request.initialBalance, 
				request.ownerName()
		);
		
		commandGateway.sendAndWait(command);
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(accountId)
				.toUri();
		
		return ResponseEntity.created(location).body(accountId);
	}
	
	@PostMapping("/{accountId}/deposit")
	public CompletableFuture<ResponseEntity<Void>> depositeMoney(
			@PathVariable String accountId,
			@RequestBody AmountRequest request) {
		return commandGateway.send(new DepositMoneyCommand (accountId, request.amount()))
                .thenApply(result -> ResponseEntity.ok().build());
	}
	
	@PostMapping("/{accountId}/withdraw")
    public CompletableFuture<ResponseEntity<Void>> withdrawMoney(
            @PathVariable String accountId, 
            @RequestBody AmountRequest request) {
        
        return commandGateway.send(new WithdrawMoneyCommand(accountId, request.amount()))
                .thenApply(result -> ResponseEntity.ok().build());
    }

    @PostMapping("/{accountId}/close")
    public CompletableFuture<ResponseEntity<Void>> closeAccount(@PathVariable String accountId) {
        return commandGateway.send(new CloseAccountCommand (accountId))
                .thenApply(result -> ResponseEntity.ok().build());
    }
    
    public record CreateAccountRequest(BigDecimal initialBalance, String ownerName) {}
    public record AmountRequest(BigDecimal amount) {}
}

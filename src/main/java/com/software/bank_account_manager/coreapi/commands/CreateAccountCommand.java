package com.software.bank_account_manager.coreapi.commands;

import java.math.BigDecimal;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CreateAccountCommand (
	@TargetAggregateIdentifier String accountId,
	BigDecimal initialBalance,
	String ownerName
) {}

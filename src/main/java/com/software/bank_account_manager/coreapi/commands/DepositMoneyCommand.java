package com.software.bank_account_manager.coreapi.commands;

import java.math.BigDecimal;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record DepositMoneyCommand (
	@TargetAggregateIdentifier String accountId,
	BigDecimal amount
) {}

package com.software.bank_account_manager.coreapi.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CloseAccountCommand (
		@TargetAggregateIdentifier String accountId
) {}

package com.software.bank_account_manager.coreapi.events;

import java.math.BigDecimal;

public record AccountCreatedEvent(
		String accountId, 
        BigDecimal initialBalance, 
        String ownerName	
) {}

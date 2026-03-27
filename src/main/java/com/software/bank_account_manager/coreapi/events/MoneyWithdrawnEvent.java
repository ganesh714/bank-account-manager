package com.software.bank_account_manager.coreapi.events;

import java.math.BigDecimal;

public record MoneyWithdrawnEvent(
		String accountId, 
        BigDecimal amount
) {}

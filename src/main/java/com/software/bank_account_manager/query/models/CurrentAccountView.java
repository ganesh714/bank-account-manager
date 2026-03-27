package com.software.bank_account_manager.query.models;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentAccountView {
	
	@Id
	private String accountId;
    private String ownerName;
    private BigDecimal balance;
    private String status;
    
    
}

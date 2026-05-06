package com.software.bank_account_manager.query.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.software.bank_account_manager.query.models.TransactionHistory;
import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    List<TransactionHistory> findByAccountIdOrderByTimestampDesc(String accountId);
}

package com.homework.transaction.domain;

import com.homework.account.domain.Currency;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("account_transaction")
public record AccountTransaction(
        @Id Long id,
        String transactionId,
        Long accountCode,
        Long counterpartyAccountCode,
        TransactionType type,
        BigDecimal amount,
        Currency currency,
        String description,
        LocalDateTime createdAt) {

    public static AccountTransaction newLeg(String transactionId, Long accountCode, Long counterpartyAccountCode,
                                            TransactionType type, BigDecimal amount, Currency currency,
                                            String description) {
        return new AccountTransaction(null, transactionId, accountCode, counterpartyAccountCode, type,
                amount, currency, description, null);
    }
}

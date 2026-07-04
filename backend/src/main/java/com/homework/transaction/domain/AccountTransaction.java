package com.homework.transaction.domain;

import com.homework.account.domain.Currency;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Table("account_transaction")
public record AccountTransaction(
        @Id Long id,
        String transactionId,
        Long accountId,
        Long counterpartyAccountId,
        TransactionType type,
        BigDecimal amount,
        Currency currency,
        String description,
        Instant createdAt) {

    public static AccountTransaction newLeg(String transactionId, Long accountId, Long counterpartyAccountId,
                                            TransactionType type, BigDecimal amount, Currency currency,
                                            String description) {
        return new AccountTransaction(null, transactionId, accountId, counterpartyAccountId, type,
                amount, currency, description, null);
    }
}

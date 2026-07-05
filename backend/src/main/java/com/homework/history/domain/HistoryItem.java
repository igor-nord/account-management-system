package com.homework.history.domain;

import com.homework.account.domain.Currency;
import com.homework.transaction.domain.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record HistoryItem(
        String transactionId,
        TransactionType type,
        BigDecimal amount,
        Currency currency,
        String description,
        Instant createdAt) {
}

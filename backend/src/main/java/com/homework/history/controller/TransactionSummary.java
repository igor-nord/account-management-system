package com.homework.history.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.homework.account.domain.Currency;
import com.homework.history.domain.HistoryItem;
import com.homework.transaction.domain.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionSummary(
        String transactionId,
        TransactionType type,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal amount,
        Currency currency,
        String description,
        LocalDateTime createdAt) {

    public static TransactionSummary of(HistoryItem item) {
        return new TransactionSummary(item.transactionId(), item.type(), item.amount(),
                item.currency(), item.description(), item.createdAt());
    }
}

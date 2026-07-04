package com.homework.transaction.infrastructure.controller;

import com.homework.transaction.domain.AccountTransaction;

import java.time.Instant;
import java.util.List;

public record TransactionResponse(String transactionId, Instant createdAt, List<LegView> legs) {

    public static TransactionResponse of(List<AccountTransaction> legs) {
        AccountTransaction first = legs.getFirst();
        return new TransactionResponse(first.transactionId(), first.createdAt(),
                legs.stream().map(LegView::of).toList());
    }
}

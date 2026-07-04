package com.homework.transaction.infrastructure.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.homework.account.domain.Currency;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;

import java.math.BigDecimal;

public record LegView(
        Long accountId,
        Long counterpartyAccountId,
        TransactionType type,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal amount,
        Currency currency,
        String description) {

    public static LegView of(AccountTransaction leg) {
        return new LegView(leg.accountId(), leg.counterpartyAccountId(), leg.type(),
                leg.amount(), leg.currency(), leg.description());
    }
}

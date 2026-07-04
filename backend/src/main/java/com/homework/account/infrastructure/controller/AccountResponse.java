package com.homework.account.infrastructure.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;

import java.math.BigDecimal;

public record AccountResponse(
        Long accountId,
        Currency currency,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal balance) {

    public static AccountResponse of(Account account) {
        return new AccountResponse(account.accountId(), account.currency(), account.balance());
    }
}

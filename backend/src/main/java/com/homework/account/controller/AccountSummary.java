package com.homework.account.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;

import java.math.BigDecimal;

public record AccountSummary(
        Long accountCode,
        Currency currency,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal balance) {

    public static AccountSummary of(Account account) {
        return new AccountSummary(account.accountCode(), account.currency(), account.balance());
    }
}

package com.homework.transaction.exception;

import com.homework.account.domain.Currency;

public class NonEuroDebitException extends RuntimeException {

    public NonEuroDebitException(Long accountId, Currency currency) {
        super("Debit is only allowed for EUR accounts (account " + accountId + " is " + currency + ")");
    }
}

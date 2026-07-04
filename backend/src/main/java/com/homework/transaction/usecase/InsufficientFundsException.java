package com.homework.transaction.usecase;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(Long accountId) {
        super("Insufficient funds in account " + accountId);
    }
}

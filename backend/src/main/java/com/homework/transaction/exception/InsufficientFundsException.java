package com.homework.transaction.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(Long accountCode) {
        super("Insufficient funds in account " + accountCode);
    }
}

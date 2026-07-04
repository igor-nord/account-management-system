package com.homework.transaction.usecase;

public class SameAccountExchangeException extends RuntimeException {

    public SameAccountExchangeException() {
        super("Source and target accounts must differ");
    }
}

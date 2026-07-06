package com.homework.transaction.exception;

public class SameAccountExchangeException extends RuntimeException {

    public SameAccountExchangeException() {
        super("Source and target accounts must differ");
    }
}

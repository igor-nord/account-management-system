package com.homework.transaction.exception;

public class ExternalLoggingException extends RuntimeException {

    public ExternalLoggingException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.homework.transaction.port;

public class ExternalLoggingException extends RuntimeException {

    public ExternalLoggingException(String message, Throwable cause) {
        super(message, cause);
    }
}

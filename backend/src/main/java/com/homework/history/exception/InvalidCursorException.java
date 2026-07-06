package com.homework.history.exception;

public class InvalidCursorException extends RuntimeException {

    public InvalidCursorException(String cursor) {
        super("Invalid history cursor: " + cursor);
    }
}

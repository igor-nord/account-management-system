package com.homework.history.infrastructure.controller;

public class InvalidCursorException extends RuntimeException {

    public InvalidCursorException(String cursor) {
        super("Invalid history cursor: " + cursor);
    }
}

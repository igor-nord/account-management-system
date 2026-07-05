package com.homework.customer.usecase;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String username) {
        super("Customer not found: " + username);
    }
}

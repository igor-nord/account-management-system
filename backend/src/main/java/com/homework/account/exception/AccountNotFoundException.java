package com.homework.account.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(Long accountCode) {
        super("Account not found: " + accountCode);
    }
}

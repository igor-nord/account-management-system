package com.homework.transaction.service;

import com.homework.transaction.domain.AccountTransaction;

import java.util.List;

public interface TransactionService {

    List<AccountTransaction> getTransaction(String username, String transactionId);
}

package com.homework.transaction.service;

import com.homework.transaction.domain.AccountTransaction;

import java.util.List;

public interface GetTransactionService {

    List<AccountTransaction> byTransactionId(String username, String transactionId);
}

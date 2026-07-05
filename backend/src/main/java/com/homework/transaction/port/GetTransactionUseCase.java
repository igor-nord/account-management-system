package com.homework.transaction.port;

import com.homework.transaction.domain.AccountTransaction;

import java.util.List;

public interface GetTransactionUseCase {

    List<AccountTransaction> byTransactionId(String username, String transactionId);
}

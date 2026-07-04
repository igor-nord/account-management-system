package com.homework.transaction.port;

import com.homework.transaction.domain.AccountTransaction;

import java.util.List;

public interface TransactionRepository {

    AccountTransaction save(AccountTransaction transaction);

    List<AccountTransaction> saveAll(List<AccountTransaction> legs);

    List<AccountTransaction> findByAccountId(Long accountId);
}

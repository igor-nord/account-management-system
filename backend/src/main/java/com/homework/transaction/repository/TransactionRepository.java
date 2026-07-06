package com.homework.transaction.repository;

import com.homework.transaction.domain.AccountTransaction;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionRepository {

    private final AccountTransactionDao accountTransactionDao;

    public TransactionRepository(AccountTransactionDao accountTransactionDao) {
        this.accountTransactionDao = accountTransactionDao;
    }

    public AccountTransaction save(AccountTransaction transaction) {
        return accountTransactionDao.save(withCreatedAt(transaction, Instant.now()));
    }

    public List<AccountTransaction> saveAll(List<AccountTransaction> legs) {
        Instant now = Instant.now();
        List<AccountTransaction> stamped = legs.stream().map(leg -> withCreatedAt(leg, now)).toList();
        List<AccountTransaction> result = new ArrayList<>();
        accountTransactionDao.saveAll(stamped).forEach(result::add);
        return result;
    }

    public List<AccountTransaction> findByAccountCode(Long accountCode) {
        return accountTransactionDao.findByAccountCodeOrderByCreatedAtAscIdAsc(accountCode);
    }

    public List<AccountTransaction> findByTransactionId(String transactionId) {
        return accountTransactionDao.findByTransactionIdOrderByIdAsc(transactionId);
    }

    private static AccountTransaction withCreatedAt(AccountTransaction transaction, Instant fallback) {
        if (transaction.createdAt() != null) {
            return transaction;
        }
        return new AccountTransaction(
                transaction.id(),
                transaction.transactionId(),
                transaction.accountCode(),
                transaction.counterpartyAccountCode(),
                transaction.type(),
                transaction.amount(),
                transaction.currency(),
                transaction.description(),
                fallback);
    }
}

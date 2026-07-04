package com.homework.transaction.infrastructure.repository;

import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.port.TransactionRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final AccountTransactionJdbcRepository jdbc;

    public TransactionRepositoryAdapter(AccountTransactionJdbcRepository jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public AccountTransaction save(AccountTransaction transaction) {
        return jdbc.save(withCreatedAt(transaction, Instant.now()));
    }

    @Override
    public List<AccountTransaction> saveAll(List<AccountTransaction> legs) {
        Instant now = Instant.now();
        List<AccountTransaction> stamped = legs.stream().map(leg -> withCreatedAt(leg, now)).toList();
        List<AccountTransaction> result = new ArrayList<>();
        jdbc.saveAll(stamped).forEach(result::add);
        return result;
    }

    @Override
    public List<AccountTransaction> findByAccountId(Long accountId) {
        return jdbc.findByAccountIdOrderByCreatedAtAscIdAsc(accountId);
    }

    private static AccountTransaction withCreatedAt(AccountTransaction transaction, Instant fallback) {
        if (transaction.createdAt() != null) {
            return transaction;
        }
        return new AccountTransaction(
                transaction.id(),
                transaction.transactionId(),
                transaction.accountId(),
                transaction.counterpartyAccountId(),
                transaction.type(),
                transaction.amount(),
                transaction.currency(),
                transaction.description(),
                fallback);
    }
}

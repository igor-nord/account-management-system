package com.homework.account.infrastructure.repository;

import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.domain.LedgerCode;
import com.homework.account.port.AccountRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class AccountRepositoryAdapter implements AccountRepository {

    private final AccountJdbcRepository jdbc;
    private final JdbcTemplate jdbcTemplate;

    public AccountRepositoryAdapter(AccountJdbcRepository jdbc, JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbc;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Account> findByAccountId(Long accountId) {
        return jdbc.findByAccountId(accountId);
    }

    @Override
    public List<Account> findByCustomerId(Long customerId) {
        return jdbc.findByCustomerId(customerId);
    }

    @Override
    public Optional<Account> findByCodeAndCurrency(LedgerCode code, Currency currency) {
        return jdbc.findByCodeAndCurrency(code, currency);
    }

    @Override
    public Account save(Account account) {
        Instant now = Instant.now();
        Long businessId = account.accountId() != null ? account.accountId() : nextAccountId();
        Instant createdAt = account.createdAt() != null ? account.createdAt() : now;

        Account toSave = new Account(
                account.id(),
                businessId,
                account.customerId(),
                account.accountType(),
                account.code(),
                account.currency(),
                account.balance(),
                createdAt,
                now);

        return jdbc.save(toSave);
    }

    private Long nextAccountId() {
        return jdbcTemplate.queryForObject("SELECT NEXT VALUE FOR account_id_seq", Long.class);
    }
}

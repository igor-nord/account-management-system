package com.homework.account.repository;

import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.domain.LedgerCode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class AccountRepository {

    private final AccountDao accountDao;
    private final JdbcTemplate jdbcTemplate;

    public AccountRepository(AccountDao accountDao, JdbcTemplate jdbcTemplate) {
        this.accountDao = accountDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Account> findByAccountId(Long accountId) {
        return accountDao.findByAccountId(accountId);
    }

    public Optional<Account> findByAccountIdAndCustomerUsername(Long accountId, String username) {
        return accountDao.findByAccountIdAndCustomerUsername(accountId, username);
    }

    public List<Account> findByCustomerUsername(String username) {
        return accountDao.findByCustomerUsername(username);
    }

    public Optional<Account> findByCodeAndCurrency(LedgerCode code, Currency currency) {
        return accountDao.findByCodeAndCurrency(code, currency);
    }

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

        return accountDao.save(toSave);
    }

    private Long nextAccountId() {
        return jdbcTemplate.queryForObject("SELECT NEXT VALUE FOR account_id_seq", Long.class);
    }
}

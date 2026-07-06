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

    public Optional<Account> findByAccountCode(Long accountCode) {
        return accountDao.findByAccountCode(accountCode);
    }

    public Optional<Account> findByAccountCodeAndCustomerUsername(Long accountCode, String username) {
        return accountDao.findByAccountCodeAndCustomerUsername(accountCode, username);
    }

    public List<Account> findByCustomerUsername(String username) {
        return accountDao.findByCustomerUsername(username);
    }

    public Optional<Account> findByLedgerCodeAndCurrency(LedgerCode ledgerCode, Currency currency) {
        return accountDao.findByLedgerCodeAndCurrency(ledgerCode, currency);
    }

    public Account save(Account account) {
        Instant now = Instant.now();
        Long businessId = account.accountCode() != null ? account.accountCode() : nextAccountCode();
        Instant createdAt = account.createdAt() != null ? account.createdAt() : now;

        Account toSave = new Account(
                account.id(),
                businessId,
                account.customerId(),
                account.accountType(),
                account.ledgerCode(),
                account.currency(),
                account.balance(),
                createdAt,
                now);

        return accountDao.save(toSave);
    }

    private Long nextAccountCode() {
        return jdbcTemplate.queryForObject("SELECT NEXT VALUE FOR account_code_seq", Long.class);
    }
}

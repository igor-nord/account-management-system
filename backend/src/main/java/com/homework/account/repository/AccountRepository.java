package com.homework.account.repository;

import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.domain.LedgerCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository {

    private final AccountDao accountDao;

    public AccountRepository(AccountDao accountDao) {
        this.accountDao = accountDao;
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
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = account.createdAt() != null ? account.createdAt() : now;

        Account accountEntity = new Account(
                account.id(),
                account.accountCode(),
                account.customerId(),
                account.accountType(),
                account.ledgerCode(),
                account.currency(),
                account.balance(),
                createdAt,
                now);

        return accountDao.save(accountEntity);
    }
}

package com.homework.account.service;

import com.homework.account.exception.AccountNotFoundException;
import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.domain.LedgerCode;
import com.homework.account.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountAccessServiceDefault implements AccountAccessService {

    private final AccountRepository accounts;

    public AccountAccessServiceDefault(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Override
    public List<Account> ownedAccounts(String username) {
        return accounts.findByCustomerUsername(username);
    }

    @Override
    public Account requireOwned(String username, Long accountId) {
        return accounts.findByAccountIdAndCustomerUsername(accountId, username)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    public Account ledgerAccount(LedgerCode code, Currency currency) {
        return accounts.findByCodeAndCurrency(code, currency)
                .orElseThrow(() -> new IllegalStateException("Missing ledger account " + code + " " + currency));
    }
}

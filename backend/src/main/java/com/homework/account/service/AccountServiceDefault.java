package com.homework.account.service;

import com.homework.account.exception.AccountNotFoundException;
import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.domain.LedgerCode;
import com.homework.account.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountServiceDefault implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceDefault(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<Account> ownedAccounts(String username) {
        return accountRepository.findByCustomerUsername(username);
    }

    @Override
    public Account requireOwned(String username, Long accountCode) {
        return accountRepository.findByAccountCodeAndCustomerUsername(accountCode, username)
                .orElseThrow(() -> new AccountNotFoundException(accountCode));
    }

    @Override
    public Account ledgerAccount(LedgerCode ledgerCode, Currency currency) {
        return accountRepository.findByLedgerCodeAndCurrency(ledgerCode, currency)
                .orElseThrow(() -> new IllegalStateException("Missing ledger account " + ledgerCode + " " + currency));
    }
}

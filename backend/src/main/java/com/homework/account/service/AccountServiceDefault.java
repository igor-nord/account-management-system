package com.homework.account.service;

import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.domain.LedgerCode;
import com.homework.account.exception.AccountNotFoundException;
import com.homework.account.repository.AccountRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceDefault implements AccountService {

    private final AccountRepository repository;

    public AccountServiceDefault(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Account> getCustomerAccounts(String username) {
        return repository.findByCustomerUsername(username);
    }

    @Override
    public Account getCustomerAccount(String username, Long accountCode) {
        return repository.findByAccountCodeAndCustomerUsername(accountCode, username)
                .orElseThrow(() -> new AccountNotFoundException(
                    String.format("Account %d not found for username %s ", accountCode, username.charAt(0) + "***")));
    }

    @Override
    public Account getLedgerAccountForCurrency(LedgerCode ledgerCode, Currency currency) {
        return repository.findByLedgerCodeAndCurrency(ledgerCode, currency)
                .orElseThrow(() -> new IllegalStateException("Missing ledger account " + ledgerCode + " " + currency));
    }
}

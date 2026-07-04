package com.homework.account.usecase;

import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.domain.LedgerCode;
import com.homework.account.port.AccountRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountAccess {

    private final AccountRepository accounts;

    public AccountAccess(AccountRepository accounts) {
        this.accounts = accounts;
    }

    public List<Account> ownedAccounts(Long customerId) {
        return accounts.findByCustomerId(customerId);
    }

    public Account requireOwned(Long customerId, Long accountId) {
        return accounts.findByAccountId(accountId)
                .filter(account -> customerId.equals(account.customerId()))
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    //TODO: remove after last clean-up
    public Account ledgerAccount(LedgerCode code, Currency currency) {
        return accounts.findByCodeAndCurrency(code, currency)
                .orElseThrow(() -> new IllegalStateException("Missing ledger account " + code + " " + currency));
    }
}

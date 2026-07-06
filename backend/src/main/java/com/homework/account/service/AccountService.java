package com.homework.account.service;

import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.domain.LedgerCode;

import java.util.List;

public interface AccountService {

    List<Account> ownedAccounts(String username);

    Account requireOwned(String username, Long accountCode);

    Account ledgerAccount(LedgerCode ledgerCode, Currency currency);
}

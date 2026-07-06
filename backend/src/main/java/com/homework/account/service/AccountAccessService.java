package com.homework.account.service;

import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.domain.LedgerCode;

import java.util.List;

public interface AccountAccessService {

    List<Account> ownedAccounts(String username);

    Account requireOwned(String username, Long accountId);

    Account ledgerAccount(LedgerCode code, Currency currency);
}

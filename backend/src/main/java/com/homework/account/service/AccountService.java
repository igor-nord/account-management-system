package com.homework.account.service;

import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.domain.LedgerCode;

import java.util.List;

public interface AccountService {

    List<Account> getCustomerAccounts(String username);

    Account getCustomerAccount(String username, Long accountCode);

    Account getLedgerAccountForCurrency(LedgerCode ledgerCode, Currency currency);
}

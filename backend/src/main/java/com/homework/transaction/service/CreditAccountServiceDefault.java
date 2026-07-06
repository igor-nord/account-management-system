package com.homework.transaction.service;

import com.homework.account.domain.Account;
import com.homework.account.domain.LedgerCode;
import com.homework.account.service.AccountService;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class CreditAccountServiceDefault implements CreditAccountService {

    private final AccountService accountAccess;
    private final LedgerWriter ledger;
    private final TsidTransactionIdGenerator trxIdGenerator;

    public CreditAccountServiceDefault(AccountService accountAccess, LedgerWriter ledger, TsidTransactionIdGenerator trxIdGenerator) {
        this.accountAccess = accountAccess;
        this.ledger = ledger;
        this.trxIdGenerator = trxIdGenerator;
    }

    @Transactional
    @Override
    public List<AccountTransaction> credit(String username, Long accountCode, BigDecimal amount, String description) {
        Account customer = accountAccess.requireOwned(username, accountCode);
        Account external = accountAccess.ledgerAccount(LedgerCode.EXTERNAL, customer.currency());
        String text = description == null || description.isBlank() ? "Deposit" : description;
        String txnId = trxIdGenerator.newTransactionId();
        List<AccountTransaction> legs = List.of(
                AccountTransaction.newLeg(txnId, customer.accountCode(), external.accountCode(),
                        TransactionType.CREDIT, amount, customer.currency(), text),
                AccountTransaction.newLeg(txnId, external.accountCode(), customer.accountCode(),
                        TransactionType.DEBIT, amount, customer.currency(), text));
        return ledger.visibleLegs(legs, Set.of(customer.accountCode()));
    }
}

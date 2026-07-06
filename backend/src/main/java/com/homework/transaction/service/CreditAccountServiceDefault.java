package com.homework.transaction.service;

import com.homework.account.domain.Account;
import com.homework.account.domain.LedgerCode;
import com.homework.account.service.AccountAccessService;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class CreditAccountServiceDefault implements CreditAccountService {

    private final AccountAccessService accountAccess;
    private final LedgerWriter ledger;
    private final TsidTransactionIdGenerator trxIdGenerator;

    public CreditAccountServiceDefault(AccountAccessService accountAccess, LedgerWriter ledger, TsidTransactionIdGenerator trxIdGenerator) {
        this.accountAccess = accountAccess;
        this.ledger = ledger;
        this.trxIdGenerator = trxIdGenerator;
    }

    @Transactional
    @Override
    public List<AccountTransaction> credit(String username, Long accountId, BigDecimal amount, String description) {
        Account customer = accountAccess.requireOwned(username, accountId);
        Account external = accountAccess.ledgerAccount(LedgerCode.EXTERNAL, customer.currency());
        String text = description == null || description.isBlank() ? "Deposit" : description;
        String txnId = trxIdGenerator.newTransactionId();
        List<AccountTransaction> legs = List.of(
                AccountTransaction.newLeg(txnId, customer.accountId(), external.accountId(),
                        TransactionType.CREDIT, amount, customer.currency(), text),
                AccountTransaction.newLeg(txnId, external.accountId(), customer.accountId(),
                        TransactionType.DEBIT, amount, customer.currency(), text));
        return ledger.visibleLegs(ledger.post(legs), Set.of(customer.accountId()));
    }
}

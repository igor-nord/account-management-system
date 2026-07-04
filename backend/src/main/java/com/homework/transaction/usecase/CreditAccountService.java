package com.homework.transaction.usecase;

import com.homework.account.domain.Account;
import com.homework.account.domain.LedgerCode;
import com.homework.account.usecase.AccountAccess;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import com.homework.transaction.port.CreditAccountUseCase;
import com.homework.transaction.port.TransactionIdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class CreditAccountService implements CreditAccountUseCase {

    private final AccountAccess accountAccess;
    private final LedgerWriter ledger;
    private final TransactionIdGenerator ids;

    public CreditAccountService(AccountAccess accountAccess, LedgerWriter ledger, TransactionIdGenerator ids) {
        this.accountAccess = accountAccess;
        this.ledger = ledger;
        this.ids = ids;
    }

    @Override
    @Transactional
    public List<AccountTransaction> credit(Long customerId, Long accountId, BigDecimal amount, String description) {
        Account customer = accountAccess.requireOwned(customerId, accountId);
        Account external = accountAccess.ledgerAccount(LedgerCode.EXTERNAL, customer.currency());
        String text = description == null || description.isBlank() ? "Deposit" : description;
        String txnId = ids.newTransactionId();
        List<AccountTransaction> legs = List.of(
                AccountTransaction.newLeg(txnId, customer.accountId(), external.accountId(),
                        TransactionType.CREDIT, amount, customer.currency(), text),
                AccountTransaction.newLeg(txnId, external.accountId(), customer.accountId(),
                        TransactionType.DEBIT, amount, customer.currency(), text));
        return LedgerWriter.visibleLegs(ledger.post(legs), Set.of(customer.accountId()));
    }
}

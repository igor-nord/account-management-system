package com.homework.transaction.usecase;

import com.homework.account.domain.Account;
import com.homework.account.domain.LedgerCode;
import com.homework.account.usecase.AccountAccess;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import com.homework.transaction.port.DebitAccountUseCase;
import com.homework.transaction.port.ExternalLoggingPort;
import com.homework.transaction.port.TransactionIdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class DebitAccountService implements DebitAccountUseCase {

    private final AccountAccess accountAccess;
    private final LedgerWriter ledger;
    private final TransactionIdGenerator ids;
    private final ExternalLoggingPort externalLogging;

    public DebitAccountService(AccountAccess accountAccess, LedgerWriter ledger, TransactionIdGenerator ids,
                               ExternalLoggingPort externalLogging) {
        this.accountAccess = accountAccess;
        this.ledger = ledger;
        this.ids = ids;
        this.externalLogging = externalLogging;
    }

    @Override
    @Transactional
    public List<AccountTransaction> debit(Long customerId, Long accountId, BigDecimal amount, String description) {
        Account customer = accountAccess.requireOwned(customerId, accountId);
        if (customer.balance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(customer.accountId());
        }
        Account external = accountAccess.ledgerAccount(LedgerCode.EXTERNAL, customer.currency());
        String text = description == null || description.isBlank() ? "Withdrawal" : description;
        String txnId = ids.newTransactionId();
        externalLogging.logBeforeDebit(txnId);
        List<AccountTransaction> legs = List.of(
                AccountTransaction.newLeg(txnId, customer.accountId(), external.accountId(),
                        TransactionType.DEBIT, amount, customer.currency(), text),
                AccountTransaction.newLeg(txnId, external.accountId(), customer.accountId(),
                        TransactionType.CREDIT, amount, customer.currency(), text));
        return LedgerWriter.visibleLegs(ledger.post(legs), Set.of(customer.accountId()));
    }
}

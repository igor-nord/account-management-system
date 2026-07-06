package com.homework.transaction.service;

import com.homework.transaction.exception.NonEuroDebitException;
import com.homework.transaction.exception.InsufficientFundsException;
import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.domain.LedgerCode;
import com.homework.account.service.AccountService;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import com.homework.transaction.integration.ExternalLoggingClient;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DebitAccountServiceDefault implements DebitAccountService {

    private final AccountService accountAccess;
    private final LedgerWriter ledger;
    private final TsidTransactionIdGenerator ids;
    private final ExternalLoggingClient externalLogging;

    public DebitAccountServiceDefault(AccountService accountAccess, LedgerWriter ledger, TsidTransactionIdGenerator ids,
                               ExternalLoggingClient externalLogging) {
        this.accountAccess = accountAccess;
        this.ledger = ledger;
        this.ids = ids;
        this.externalLogging = externalLogging;
    }

    @Transactional
    @Override
    public List<AccountTransaction> debit(String username, Long accountCode, BigDecimal amount, String description) {
        Account customerAccount = accountAccess.requireOwned(username, accountCode);
        verifyInputData(amount, customerAccount);
        Account external = accountAccess.ledgerAccount(LedgerCode.EXTERNAL, customerAccount.currency());
        String text = description == null || description.isBlank() ? "Withdrawal" : description;
        String txnId = ids.newTransactionId();
        externalLogging.logBeforeDebit(txnId);
        List<AccountTransaction> legs = List.of(
                AccountTransaction.newLeg(txnId, customerAccount.accountCode(), external.accountCode(),
                        TransactionType.DEBIT, amount, customerAccount.currency(), text),
                AccountTransaction.newLeg(txnId, external.accountCode(), customerAccount.accountCode(),
                        TransactionType.CREDIT, amount, customerAccount.currency(), text));
        return ledger.visibleLegs(legs, Set.of(customerAccount.accountCode()));
    }

    private static void verifyInputData(BigDecimal amount, Account customerAccount) {
        if (customerAccount.currency() != Currency.EUR) {
            throw new NonEuroDebitException(customerAccount.accountCode(), customerAccount.currency());
        }
        if (customerAccount.balance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(customerAccount.accountCode());
        }
    }
}

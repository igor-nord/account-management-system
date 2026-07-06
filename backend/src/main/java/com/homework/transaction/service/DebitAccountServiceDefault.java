package com.homework.transaction.service;

import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.domain.LedgerCode;
import com.homework.account.service.AccountService;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import com.homework.transaction.exception.InsufficientFundsException;
import com.homework.transaction.exception.NonEuroDebitException;
import com.homework.transaction.integration.ExternalLoggingClient;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DebitAccountServiceDefault implements DebitAccountService {

    private final AccountService accountService;
    private final LedgerHandlerService ledgerHandler;
    private final TsidTransactionIdGenerator trxIdGenerator;
    private final ExternalLoggingClient externalLogging;

    public DebitAccountServiceDefault(AccountService accountService, LedgerHandlerService ledgerHandler, TsidTransactionIdGenerator trxIdGenerator,
                               ExternalLoggingClient externalLogging) {
        this.accountService = accountService;
        this.ledgerHandler = ledgerHandler;
        this.trxIdGenerator = trxIdGenerator;
        this.externalLogging = externalLogging;
    }

    @Transactional
    @Override
    public List<AccountTransaction> debit(String username, Long accountCode, BigDecimal amount, String description) {
        Account customerAccount = accountService.getCustomerAccount(username, accountCode);
        verifyInputData(amount, customerAccount);
        Account external = accountService.getLedgerAccountForCurrency(LedgerCode.EXTERNAL, customerAccount.currency());
        String text = description == null || description.isBlank() ? "Withdrawal" : description;
        String txnId = trxIdGenerator.newTransactionId();
        externalLogging.logBeforeDebit(txnId);
        List<AccountTransaction> legs = List.of(
                AccountTransaction.newLeg(txnId, customerAccount.accountCode(), external.accountCode(),
                        TransactionType.DEBIT, amount, customerAccount.currency(), text),
                AccountTransaction.newLeg(txnId, external.accountCode(), customerAccount.accountCode(),
                        TransactionType.CREDIT, amount, customerAccount.currency(), text));
        return ledgerHandler.handleAllTxLegs(legs, Set.of(customerAccount.accountCode()));
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

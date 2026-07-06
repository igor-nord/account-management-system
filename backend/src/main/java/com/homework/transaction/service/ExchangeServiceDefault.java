package com.homework.transaction.service;

import com.homework.account.domain.Account;
import com.homework.account.domain.LedgerCode;
import com.homework.account.service.AccountService;
import com.homework.exchange.service.CurrencyExchangeService;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import com.homework.transaction.exception.InsufficientFundsException;
import com.homework.transaction.exception.SameAccountExchangeException;
import com.homework.transaction.integration.ExternalLoggingClient;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExchangeServiceDefault implements ExchangeService {

    private final AccountService accountService;
    private final LedgerHandlerService ledgerWriter;
    private final TsidTransactionIdGenerator txIdGenerator;
    private final ExternalLoggingClient externalLoggingClient;
    private final CurrencyExchangeService exchangeService;

    public ExchangeServiceDefault(AccountService accountService, LedgerHandlerService ledgerHandler, TsidTransactionIdGenerator txIdGenerator,
                           ExternalLoggingClient externalLoggingClient, CurrencyExchangeService exchangeService) {
        this.accountService = accountService;
        this.ledgerWriter = ledgerHandler;
        this.txIdGenerator = txIdGenerator;
        this.externalLoggingClient = externalLoggingClient;
        this.exchangeService = exchangeService;
    }

    @Transactional
    @Override
    public List<AccountTransaction> exchange(String username, Long sourceAccountCode, Long targetAccountCode,
                                             BigDecimal amount) {
        if (sourceAccountCode.equals(targetAccountCode)) {
            throw new SameAccountExchangeException();
        }
        Account sourceAccount = accountService.getCustomerAccount(username, sourceAccountCode);
        Account targetAccount = accountService.getCustomerAccount(username, targetAccountCode);

        if (sourceAccount.balance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(sourceAccount.accountCode());
        }
        BigDecimal converted = exchangeService.convert(amount, sourceAccount.currency(), targetAccount.currency());
        Account sourceCurrencyLedgerAccount = accountService.getLedgerAccountForCurrency(LedgerCode.FX_CLEARING, sourceAccount.currency());
        Account targetCurrencyLedgerAccountTarget = accountService.getLedgerAccountForCurrency(LedgerCode.FX_CLEARING, targetAccount.currency());

        String txnId = txIdGenerator.newTransactionId();

        externalLoggingClient.logBeforeDebit(txnId);

        String description = "Exchange " + sourceAccount.currency() + "->" + targetAccount.currency();
        List<AccountTransaction> legs = List.of(
                AccountTransaction.newLeg(txnId, sourceAccount.accountCode(), sourceCurrencyLedgerAccount.accountCode(),
                        TransactionType.DEBIT, amount, sourceAccount.currency(), description),
                AccountTransaction.newLeg(txnId, sourceCurrencyLedgerAccount.accountCode(), sourceAccount.accountCode(),
                        TransactionType.CREDIT, amount, sourceAccount.currency(), description),
                AccountTransaction.newLeg(txnId, targetCurrencyLedgerAccountTarget.accountCode(), targetAccount.accountCode(),
                        TransactionType.DEBIT, converted, targetAccount.currency(), description),
                AccountTransaction.newLeg(txnId, targetAccount.accountCode(), targetCurrencyLedgerAccountTarget.accountCode(),
                        TransactionType.CREDIT, converted, targetAccount.currency(), description));

        return ledgerWriter.handleAllTxLegs(legs, Set.of(sourceAccount.accountCode(), targetAccount.accountCode()));
    }
}

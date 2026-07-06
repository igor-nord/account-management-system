package com.homework.transaction.service;

import com.homework.transaction.exception.SameAccountExchangeException;
import com.homework.transaction.exception.InsufficientFundsException;
import com.homework.account.domain.Account;
import com.homework.account.domain.LedgerCode;
import com.homework.account.service.AccountAccessService;
import com.homework.exchange.service.ExchangeRateService;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import com.homework.transaction.integration.ExternalLoggingClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class ExchangeServiceDefault implements ExchangeService {

    private final AccountAccessService accountAccess;
    private final LedgerWriter ledger;
    private final TsidTransactionIdGenerator ids;
    private final ExternalLoggingClient externalLogging;
    private final ExchangeRateService exchangeRates;

    public ExchangeServiceDefault(AccountAccessService accountAccess, LedgerWriter ledger, TsidTransactionIdGenerator ids,
                           ExternalLoggingClient externalLogging, ExchangeRateService exchangeRates) {
        this.accountAccess = accountAccess;
        this.ledger = ledger;
        this.ids = ids;
        this.externalLogging = externalLogging;
        this.exchangeRates = exchangeRates;
    }

    @Transactional
    @Override
    public List<AccountTransaction> exchange(String username, Long sourceAccountId, Long targetAccountId,
                                             BigDecimal amount) {
        if (sourceAccountId.equals(targetAccountId)) {
            throw new SameAccountExchangeException();
        }
        Account source = accountAccess.requireOwned(username, sourceAccountId);
        Account target = accountAccess.requireOwned(username, targetAccountId);
        if (source.balance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(source.accountId());
        }
        BigDecimal converted = exchangeRates.convert(amount, source.currency(), target.currency());
        Account fxSource = accountAccess.ledgerAccount(LedgerCode.FX_CLEARING, source.currency());
        Account fxTarget = accountAccess.ledgerAccount(LedgerCode.FX_CLEARING, target.currency());
        String txnId = ids.newTransactionId();
        externalLogging.logBeforeDebit(txnId);
        String description = "Exchange " + source.currency() + "->" + target.currency();
        List<AccountTransaction> legs = List.of(
                AccountTransaction.newLeg(txnId, source.accountId(), fxSource.accountId(),
                        TransactionType.DEBIT, amount, source.currency(), description),
                AccountTransaction.newLeg(txnId, fxSource.accountId(), source.accountId(),
                        TransactionType.CREDIT, amount, source.currency(), description),
                AccountTransaction.newLeg(txnId, fxTarget.accountId(), target.accountId(),
                        TransactionType.DEBIT, converted, target.currency(), description),
                AccountTransaction.newLeg(txnId, target.accountId(), fxTarget.accountId(),
                        TransactionType.CREDIT, converted, target.currency(), description));
        return ledger.visibleLegs(ledger.post(legs), Set.of(source.accountId(), target.accountId()));
    }
}

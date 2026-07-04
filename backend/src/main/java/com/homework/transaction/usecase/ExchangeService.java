package com.homework.transaction.usecase;

import com.homework.account.domain.Account;
import com.homework.account.domain.LedgerCode;
import com.homework.account.usecase.AccountAccess;
import com.homework.exchange.port.ExchangeRateService;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import com.homework.transaction.port.ExchangeUseCase;
import com.homework.transaction.port.ExternalLoggingPort;
import com.homework.transaction.port.TransactionIdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class ExchangeService implements ExchangeUseCase {

    private final AccountAccess accountAccess;
    private final LedgerWriter ledger;
    private final TransactionIdGenerator ids;
    private final ExternalLoggingPort externalLogging;
    private final ExchangeRateService exchangeRates;

    public ExchangeService(AccountAccess accountAccess, LedgerWriter ledger, TransactionIdGenerator ids,
                           ExternalLoggingPort externalLogging, ExchangeRateService exchangeRates) {
        this.accountAccess = accountAccess;
        this.ledger = ledger;
        this.ids = ids;
        this.externalLogging = externalLogging;
        this.exchangeRates = exchangeRates;
    }

    @Override
    @Transactional
    public List<AccountTransaction> exchange(Long customerId, Long sourceAccountId, Long targetAccountId,
                                             BigDecimal amount) {
        if (sourceAccountId.equals(targetAccountId)) {
            throw new SameAccountExchangeException();
        }
        Account source = accountAccess.requireOwned(customerId, sourceAccountId);
        Account target = accountAccess.requireOwned(customerId, targetAccountId);
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
        return LedgerWriter.visibleLegs(ledger.post(legs), Set.of(source.accountId(), target.accountId()));
    }
}

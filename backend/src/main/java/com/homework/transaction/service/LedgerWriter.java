package com.homework.transaction.service;

import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.repository.AccountRepository;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import com.homework.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LedgerWriter {

    private final TransactionRepository transactions;
    private final AccountRepository accounts;

    public LedgerWriter(TransactionRepository transactions, AccountRepository accounts) {
        this.transactions = transactions;
        this.accounts = accounts;
    }

    public List<AccountTransaction> post(List<AccountTransaction> legs) {
        assertZeroSumPerCurrency(legs);
        List<AccountTransaction> saved = transactions.saveAll(legs);
        legs.forEach(this::applyToBalance);
        return saved;
    }

    public List<AccountTransaction> visibleLegs(List<AccountTransaction> legs, Set<Long> customerAccountIds) {
        return legs.stream().filter(leg -> customerAccountIds.contains(leg.accountId())).toList();
    }

    private void applyToBalance(AccountTransaction leg) {
        Account account = accounts.findByAccountId(leg.accountId()).orElseThrow();
        BigDecimal delta = leg.type() == TransactionType.CREDIT ? leg.amount() : leg.amount().negate();
        accounts.save(account.withBalance(account.balance().add(delta)));
    }

    private void assertZeroSumPerCurrency(List<AccountTransaction> legs) {
        Map<Currency, BigDecimal> sums = legs.stream().collect(Collectors.groupingBy(
                AccountTransaction::currency,
                Collectors.reducing(BigDecimal.ZERO, LedgerWriter::signedAmount, BigDecimal::add)));
        boolean balanced = sums.values().stream().allMatch(sum -> sum.compareTo(BigDecimal.ZERO) == 0);
        if (!balanced) {
            throw new IllegalStateException("Ledger legs do not balance per currency: " + sums);
        }
    }

    private static BigDecimal signedAmount(AccountTransaction leg) {
        return leg.type() == TransactionType.CREDIT ? leg.amount() : leg.amount().negate();
    }
}

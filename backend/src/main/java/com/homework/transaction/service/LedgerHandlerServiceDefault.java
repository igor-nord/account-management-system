package com.homework.transaction.service;

import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.repository.AccountRepository;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import com.homework.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class LedgerHandlerServiceDefault implements LedgerHandlerService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public LedgerHandlerServiceDefault(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<AccountTransaction> handleAllTxLegs(List<AccountTransaction> legs, Set<Long> customerAccountCodes) {
        assertZeroSumPerCurrency(legs);
        List<AccountTransaction> persistedTransactions = transactionRepository.saveAll(legs);
        legs.forEach(this::applyToBalance);

        return persistedTransactions.stream()
            .filter(leg -> customerAccountCodes.contains(leg.accountCode()))
            .toList();
    }

    private void applyToBalance(AccountTransaction leg) {
        Account account = accountRepository.findByAccountCode(leg.accountCode()).orElseThrow();
        BigDecimal delta = leg.type() == TransactionType.CREDIT ? leg.amount() : leg.amount().negate();
        accountRepository.save(account.withBalance(account.balance().add(delta)));
    }

    private void assertZeroSumPerCurrency(List<AccountTransaction> legs) {
        Map<Currency, BigDecimal> sums = legs.stream()
            .collect(Collectors.groupingBy(
                AccountTransaction::currency,
                Collectors.reducing(BigDecimal.ZERO, LedgerHandlerServiceDefault::signedAmount, BigDecimal::add)));
        boolean balanced = sums.values().stream().allMatch(sum -> sum.compareTo(BigDecimal.ZERO) == 0);
        if (!balanced) {
            throw new IllegalStateException("Ledger legs do not balance per currency: " + sums);
        }
    }

    private static BigDecimal signedAmount(AccountTransaction leg) {
        return leg.type() == TransactionType.CREDIT ? leg.amount() : leg.amount().negate();
    }
}

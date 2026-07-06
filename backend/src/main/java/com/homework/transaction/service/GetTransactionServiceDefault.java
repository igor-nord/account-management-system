package com.homework.transaction.service;

import com.homework.transaction.exception.TransactionNotFoundException;
import com.homework.account.domain.Account;
import com.homework.account.service.AccountService;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GetTransactionServiceDefault implements GetTransactionService {

    private final TransactionRepository transactions;
    private final AccountService accountAccess;

    public GetTransactionServiceDefault(TransactionRepository transactions, AccountService accountAccess) {
        this.transactions = transactions;
        this.accountAccess = accountAccess;
    }

    @Override
    public List<AccountTransaction> byTransactionId(String username, String transactionId) {
        List<AccountTransaction> legs = transactions.findByTransactionId(transactionId);
        if (legs.isEmpty()) {
            throw new TransactionNotFoundException(transactionId);
        }
        Set<Long> owned = accountAccess.ownedAccounts(username).stream()
                .map(Account::accountCode).collect(Collectors.toSet());
        List<AccountTransaction> visible = legs.stream()
                .filter(leg -> owned.contains(leg.accountCode())).toList();
        if (visible.isEmpty()) {
            throw new TransactionNotFoundException(transactionId);
        }
        return visible;
    }
}

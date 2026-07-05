package com.homework.transaction.usecase;

import com.homework.account.domain.Account;
import com.homework.account.usecase.AccountAccess;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.port.GetTransactionUseCase;
import com.homework.transaction.port.TransactionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GetTransactionService implements GetTransactionUseCase {

    private final TransactionRepository transactions;
    private final AccountAccess accountAccess;

    public GetTransactionService(TransactionRepository transactions, AccountAccess accountAccess) {
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
                .map(Account::accountId).collect(Collectors.toSet());
        List<AccountTransaction> visible = legs.stream()
                .filter(leg -> owned.contains(leg.accountId())).toList();
        if (visible.isEmpty()) {
            throw new TransactionNotFoundException(transactionId);
        }
        return visible;
    }
}

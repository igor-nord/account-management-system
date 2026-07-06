package com.homework.transaction.service;

import com.homework.account.domain.Account;
import com.homework.account.service.AccountService;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.exception.TransactionNotFoundException;
import com.homework.transaction.repository.TransactionRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceDefault implements TransactionService {

    private final TransactionRepository transactions;
    private final AccountService accountService;

    public TransactionServiceDefault(TransactionRepository transactions, AccountService accountService) {
        this.transactions = transactions;
        this.accountService = accountService;
    }

    @Override
    public List<AccountTransaction> getTransaction(String username, String transactionId) {
        List<AccountTransaction> legs = transactions.findByTransactionId(transactionId);
        if (legs.isEmpty()) {
            throw new TransactionNotFoundException(transactionId);
        }
        Set<Long> customerAccountCodes =
            accountService.getCustomerAccounts(username)
                .stream()
                .map(Account::accountCode)
                .collect(Collectors.toSet());

        List<AccountTransaction> visible = legs.stream()
                .filter(leg -> customerAccountCodes.contains(leg.accountCode())).toList();
        if (visible.isEmpty()) {
            throw new TransactionNotFoundException(transactionId);
        }
        return visible;
    }
}

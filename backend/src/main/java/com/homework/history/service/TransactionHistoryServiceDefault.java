package com.homework.history.service;

import com.homework.account.domain.Account;
import com.homework.account.service.AccountService;
import com.homework.history.domain.HistoryCursor;
import com.homework.history.domain.HistoryItem;
import com.homework.history.domain.HistoryPage;
import com.homework.history.repository.AccountHistoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TransactionHistoryServiceDefault implements TransactionHistoryService {

    private final AccountService accountService;
    private final AccountHistoryRepository historyRepository;

    public TransactionHistoryServiceDefault(AccountService accountService, AccountHistoryRepository historyRepository) {
        this.accountService = accountService;
        this.historyRepository = historyRepository;
    }

    @Override
    public HistoryPage history(String username, Long accountCode, int limit, HistoryCursor cursor) {
        Account account = accountService.getCustomerAccount(username, accountCode);
        List<HistoryItem> items = historyRepository.getHistoryPage(account.accountCode(), limit, cursor);
        HistoryCursor next = items.size() == limit
                ? new HistoryCursor(items.getLast().createdAt(), items.getLast().transactionId())
                : null;
        return new HistoryPage(items, next);
    }
}

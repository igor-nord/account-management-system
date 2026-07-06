package com.homework.history.service;

import com.homework.account.domain.Account;
import com.homework.account.service.AccountService;
import com.homework.history.domain.HistoryCursor;
import com.homework.history.domain.HistoryItem;
import com.homework.history.domain.HistoryPage;
import com.homework.history.repository.AccountHistoryJdbcQuery;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetTransactionHistoryServiceDefault implements GetTransactionHistoryService {

    private final AccountService accountAccess;
    private final AccountHistoryJdbcQuery query;

    public GetTransactionHistoryServiceDefault(AccountService accountAccess, AccountHistoryJdbcQuery query) {
        this.accountAccess = accountAccess;
        this.query = query;
    }

    @Override
    public HistoryPage history(String username, Long accountCode, int limit, HistoryCursor cursor) {
        Account account = accountAccess.requireOwned(username, accountCode);
        List<HistoryItem> items = query.page(account.accountCode(), limit, cursor);
        HistoryCursor next = items.size() == limit
                ? new HistoryCursor(items.getLast().createdAt(), items.getLast().transactionId())
                : null;
        return new HistoryPage(items, next);
    }
}

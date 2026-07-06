package com.homework.history.service;

import com.homework.account.domain.Account;
import com.homework.account.service.AccountAccessService;
import com.homework.history.domain.HistoryCursor;
import com.homework.history.domain.HistoryItem;
import com.homework.history.domain.HistoryPage;
import com.homework.history.repository.AccountHistoryJdbcQuery;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetTransactionHistoryServiceDefault implements GetTransactionHistoryService {

    private final AccountAccessService accountAccess;
    private final AccountHistoryJdbcQuery query;

    public GetTransactionHistoryServiceDefault(AccountAccessService accountAccess, AccountHistoryJdbcQuery query) {
        this.accountAccess = accountAccess;
        this.query = query;
    }

    @Override
    public HistoryPage history(String username, Long accountId, int limit, HistoryCursor cursor) {
        Account account = accountAccess.requireOwned(username, accountId);
        List<HistoryItem> items = query.page(account.accountId(), limit, cursor);
        HistoryCursor next = items.size() == limit
                ? new HistoryCursor(items.getLast().createdAt(), items.getLast().transactionId())
                : null;
        return new HistoryPage(items, next);
    }
}

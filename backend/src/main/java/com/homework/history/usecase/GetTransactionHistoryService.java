package com.homework.history.usecase;

import com.homework.account.domain.Account;
import com.homework.account.usecase.AccountAccess;
import com.homework.history.domain.HistoryCursor;
import com.homework.history.domain.HistoryItem;
import com.homework.history.domain.HistoryPage;
import com.homework.history.port.AccountHistoryQuery;
import com.homework.history.port.GetTransactionHistoryUseCase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetTransactionHistoryService implements GetTransactionHistoryUseCase {

    private final AccountAccess accountAccess;
    private final AccountHistoryQuery query;

    public GetTransactionHistoryService(AccountAccess accountAccess, AccountHistoryQuery query) {
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

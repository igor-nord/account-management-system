package com.homework.history.service;

import com.homework.account.domain.Account;
import com.homework.account.service.AccountAccessService;
import com.homework.history.domain.BalancePoint;
import com.homework.history.repository.AccountHistoryJdbcQuery;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetBalanceSeriesServiceDefault implements GetBalanceSeriesService {

    private final AccountAccessService accountAccess;
    private final AccountHistoryJdbcQuery query;

    public GetBalanceSeriesServiceDefault(AccountAccessService accountAccess, AccountHistoryJdbcQuery query) {
        this.accountAccess = accountAccess;
        this.query = query;
    }

    @Override
    public List<BalancePoint> balanceSeries(String username, Long accountId) {
        Account account = accountAccess.requireOwned(username, accountId);
        return query.balanceSeries(account.accountId());
    }
}

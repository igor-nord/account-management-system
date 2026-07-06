package com.homework.history.service;

import com.homework.account.domain.Account;
import com.homework.account.service.AccountService;
import com.homework.history.domain.BalancePoint;
import com.homework.history.repository.AccountHistoryJdbcQuery;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetBalanceSeriesServiceDefault implements GetBalanceSeriesService {

    private final AccountService accountAccess;
    private final AccountHistoryJdbcQuery query;

    public GetBalanceSeriesServiceDefault(AccountService accountAccess, AccountHistoryJdbcQuery query) {
        this.accountAccess = accountAccess;
        this.query = query;
    }

    @Override
    public List<BalancePoint> balanceSeries(String username, Long accountCode) {
        Account account = accountAccess.requireOwned(username, accountCode);
        return query.balanceSeries(account.accountCode());
    }
}

package com.homework.history.usecase;

import com.homework.account.domain.Account;
import com.homework.account.usecase.AccountAccess;
import com.homework.history.domain.BalancePoint;
import com.homework.history.port.AccountHistoryQuery;
import com.homework.history.port.GetBalanceSeriesUseCase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetBalanceSeriesService implements GetBalanceSeriesUseCase {

    private final AccountAccess accountAccess;
    private final AccountHistoryQuery query;

    public GetBalanceSeriesService(AccountAccess accountAccess, AccountHistoryQuery query) {
        this.accountAccess = accountAccess;
        this.query = query;
    }

    @Override
    public List<BalancePoint> balanceSeries(Long customerId, Long accountId) {
        Account account = accountAccess.requireOwned(customerId, accountId);
        return query.balanceSeries(account.accountId());
    }
}

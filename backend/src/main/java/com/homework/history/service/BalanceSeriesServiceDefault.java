package com.homework.history.service;

import com.homework.account.domain.Account;
import com.homework.account.service.AccountService;
import com.homework.history.domain.BalancePoint;
import com.homework.history.repository.AccountHistoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BalanceSeriesServiceDefault implements BalanceSeriesService {

    private final AccountService accountService;
    private final AccountHistoryRepository historyRepository;

    public BalanceSeriesServiceDefault(AccountService accountService, AccountHistoryRepository historyRepository) {
        this.accountService = accountService;
        this.historyRepository = historyRepository;
    }

    @Override
    public List<BalancePoint> balanceSeries(String username, Long accountCode) {
        Account account = accountService.getCustomerAccount(username, accountCode);
        return historyRepository.getBalanceSeries(account.accountCode());
    }
}

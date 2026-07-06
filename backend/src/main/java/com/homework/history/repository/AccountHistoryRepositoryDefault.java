package com.homework.history.repository;

import com.homework.history.domain.BalancePoint;
import com.homework.history.domain.HistoryCursor;
import com.homework.history.domain.HistoryItem;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class AccountHistoryRepositoryDefault implements AccountHistoryRepository {

  private final AccountHistoryDao accountHistoryDao;

  public AccountHistoryRepositoryDefault(AccountHistoryDao accountHistoryDao) {
    this.accountHistoryDao = accountHistoryDao;
  }

  @Override
  public List<HistoryItem> getHistoryPage(Long accountCode, int limit, HistoryCursor cursor) {
    return accountHistoryDao.getHistoryPage(accountCode, limit, cursor);
  }

  @Override
  public List<BalancePoint> getBalanceSeries(Long accountCode) {
    return accountHistoryDao.getBalanceSeries(accountCode);
  }
}

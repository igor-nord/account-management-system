package com.homework.history.repository;

import com.homework.history.domain.BalancePoint;
import com.homework.history.domain.HistoryCursor;
import com.homework.history.domain.HistoryItem;
import java.util.List;

public interface AccountHistoryRepository {

  List<HistoryItem> getHistoryPage(Long accountCode, int limit, HistoryCursor cursor);

  List<BalancePoint> getBalanceSeries(Long accountCode);

}

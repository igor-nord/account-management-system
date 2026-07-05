package com.homework.history.port;

import com.homework.history.domain.BalancePoint;
import com.homework.history.domain.HistoryCursor;
import com.homework.history.domain.HistoryItem;

import java.util.List;

public interface AccountHistoryQuery {

    List<HistoryItem> page(Long accountId, int limit, HistoryCursor cursor);

    List<BalancePoint> balanceSeries(Long accountId);
}

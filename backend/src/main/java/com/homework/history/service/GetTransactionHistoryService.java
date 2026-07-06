package com.homework.history.service;

import com.homework.history.domain.HistoryCursor;
import com.homework.history.domain.HistoryPage;

public interface GetTransactionHistoryService {

    HistoryPage history(String username, Long accountId, int limit, HistoryCursor cursor);
}

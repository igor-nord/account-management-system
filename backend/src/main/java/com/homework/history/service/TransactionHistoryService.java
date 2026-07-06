package com.homework.history.service;

import com.homework.history.domain.HistoryCursor;
import com.homework.history.domain.HistoryPage;

public interface TransactionHistoryService {

    HistoryPage history(String username, Long accountCode, int limit, HistoryCursor cursor);
}

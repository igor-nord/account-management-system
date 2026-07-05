package com.homework.history.port;

import com.homework.history.domain.HistoryCursor;
import com.homework.history.domain.HistoryPage;

public interface GetTransactionHistoryUseCase {

    HistoryPage history(String username, Long accountId, int limit, HistoryCursor cursor);
}

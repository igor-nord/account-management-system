package com.homework.history.infrastructure.controller;

import com.homework.common.web.CurrentUsername;
import com.homework.history.domain.HistoryCursor;
import com.homework.history.port.GetBalanceSeriesUseCase;
import com.homework.history.port.GetTransactionHistoryUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
class HistoryController {

    private final GetTransactionHistoryUseCase history;
    private final GetBalanceSeriesUseCase balanceSeries;

    HistoryController(GetTransactionHistoryUseCase history, GetBalanceSeriesUseCase balanceSeries) {
        this.history = history;
        this.balanceSeries = balanceSeries;
    }

    @GetMapping("/transactions")
    HistoryPageResponse transactions(@CurrentUsername String username,
                                     @RequestHeader("X-Account-Id") Long accountId,
                                     @RequestParam(defaultValue = "20") int limit,
                                     @RequestParam(required = false) String cursor) {
        int capped = Math.clamp(limit, 1, 100);
        HistoryCursor decoded = (cursor == null || cursor.isBlank()) ? null : CursorCodec.decode(cursor);
        return HistoryPageResponse.of(history.history(username, accountId, capped, decoded));
    }

    @GetMapping("/balance-series")
    BalanceSeriesResponse balanceSeries(@CurrentUsername String username,
                                        @RequestHeader("X-Account-Id") Long accountId) {
        return BalanceSeriesResponse.of(balanceSeries.balanceSeries(username, accountId));
    }
}

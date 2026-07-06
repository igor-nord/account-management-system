package com.homework.history.controller;

import com.homework.common.web.CurrentUsername;
import com.homework.history.domain.HistoryCursor;
import com.homework.history.service.BalanceSeriesService;
import com.homework.history.service.TransactionHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
class HistoryController {

    private final TransactionHistoryService transactionHistoryService;
    private final BalanceSeriesService balanceSeries;

    HistoryController(TransactionHistoryService transactionHistoryService, BalanceSeriesService balanceSeries) {
        this.transactionHistoryService = transactionHistoryService;
        this.balanceSeries = balanceSeries;
    }

    @GetMapping("/transactions")
    HistoryPageResponse getTransactions(@CurrentUsername String username,
                                     @RequestHeader("X-Account-Code") Long accountCode,
                                     @RequestParam(defaultValue = "20") int limit,
                                     @RequestParam(required = false) String cursor) {
        int capped = Math.clamp(limit, 1, 100);
        HistoryCursor decoded = (cursor == null || cursor.isBlank()) ? null : CursorCodec.decode(cursor);
        return HistoryPageResponse.of(transactionHistoryService.history(username, accountCode, capped, decoded));
    }

    @GetMapping("/balance-series")
    BalanceSeriesResponse getBalanceSeries(@CurrentUsername String username,
                                        @RequestHeader("X-Account-Code") Long accountCode) {
        return BalanceSeriesResponse.of(balanceSeries.balanceSeries(username, accountCode));
    }
}

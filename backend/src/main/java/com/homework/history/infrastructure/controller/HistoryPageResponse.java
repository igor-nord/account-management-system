package com.homework.history.infrastructure.controller;

import com.homework.history.domain.HistoryPage;

import java.util.List;

public record HistoryPageResponse(List<TransactionSummary> items, String nextCursor) {

    public static HistoryPageResponse of(HistoryPage page) {
        String next = page.nextCursor() == null ? null : CursorCodec.encode(page.nextCursor());
        return new HistoryPageResponse(page.items().stream().map(TransactionSummary::of).toList(), next);
    }
}

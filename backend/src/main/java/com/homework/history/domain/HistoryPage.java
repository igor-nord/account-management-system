package com.homework.history.domain;

import java.util.List;

public record HistoryPage(List<HistoryItem> items, HistoryCursor nextCursor) {
}

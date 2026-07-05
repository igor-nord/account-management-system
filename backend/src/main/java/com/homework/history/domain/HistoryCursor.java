package com.homework.history.domain;

import java.time.Instant;

public record HistoryCursor(Instant createdAt, String transactionId) {
}

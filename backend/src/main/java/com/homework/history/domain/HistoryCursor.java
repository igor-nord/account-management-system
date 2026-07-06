package com.homework.history.domain;

import java.time.LocalDateTime;

public record HistoryCursor(LocalDateTime createdAt, String transactionId) {
}

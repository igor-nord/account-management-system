package com.homework.history.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record BalancePoint(Instant time, BigDecimal balance) {
}

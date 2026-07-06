package com.homework.history.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BalancePoint(LocalDateTime time, BigDecimal balance) {
}

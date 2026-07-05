package com.homework.history.infrastructure.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.homework.history.domain.BalancePoint;

import java.math.BigDecimal;
import java.time.Instant;

public record BalanceSnapshot(
        Instant time,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal balance) {

    public static BalanceSnapshot of(BalancePoint point) {
        return new BalanceSnapshot(point.time(), point.balance());
    }
}

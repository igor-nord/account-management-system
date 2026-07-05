package com.homework.history.infrastructure.controller;

import com.homework.history.domain.BalancePoint;

import java.util.List;

public record BalanceSeriesResponse(List<BalanceSnapshot> points) {

    public static BalanceSeriesResponse of(List<BalancePoint> points) {
        return new BalanceSeriesResponse(points.stream().map(BalanceSnapshot::of).toList());
    }
}

package com.homework.history.service;

import com.homework.history.domain.BalancePoint;

import java.util.List;

public interface BalanceSeriesService {

    List<BalancePoint> balanceSeries(String username, Long accountCode);
}

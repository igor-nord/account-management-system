package com.homework.history.port;

import com.homework.history.domain.BalancePoint;

import java.util.List;

public interface GetBalanceSeriesUseCase {

    List<BalancePoint> balanceSeries(String username, Long accountId);
}

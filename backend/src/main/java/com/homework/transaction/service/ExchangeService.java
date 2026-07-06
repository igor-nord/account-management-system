package com.homework.transaction.service;

import com.homework.transaction.domain.AccountTransaction;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeService {

    List<AccountTransaction> exchange(String username, Long sourceAccountCode, Long targetAccountCode, BigDecimal amount);
}

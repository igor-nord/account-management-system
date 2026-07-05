package com.homework.transaction.port;

import com.homework.transaction.domain.AccountTransaction;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeUseCase {

    List<AccountTransaction> exchange(String username, Long sourceAccountId, Long targetAccountId, BigDecimal amount);
}

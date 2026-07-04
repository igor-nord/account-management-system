package com.homework.transaction.port;

import com.homework.transaction.domain.AccountTransaction;

import java.math.BigDecimal;
import java.util.List;

public interface CreditAccountUseCase {

    List<AccountTransaction> credit(Long customerId, Long accountId, BigDecimal amount, String description);
}

package com.homework.transaction.service;

import com.homework.transaction.domain.AccountTransaction;

import java.math.BigDecimal;
import java.util.List;

public interface CreditAccountService {

    List<AccountTransaction> credit(String username, Long accountCode, BigDecimal amount, String description);
}

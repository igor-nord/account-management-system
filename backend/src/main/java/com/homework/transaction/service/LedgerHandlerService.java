package com.homework.transaction.service;

import com.homework.transaction.domain.AccountTransaction;
import java.util.List;
import java.util.Set;

public interface LedgerHandlerService {

  List<AccountTransaction> handleAllTxLegs(List<AccountTransaction> legs,
      Set<Long> customerAccountCodes);
}

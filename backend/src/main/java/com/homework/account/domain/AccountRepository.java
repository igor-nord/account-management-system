package com.homework.account.domain;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Optional<Account> findByAccountId(Long accountId);

    List<Account> findByCustomerId(Long customerId);

    Optional<Account> findByCodeAndCurrency(LedgerCode code, Currency currency);

    Account save(Account account);
}

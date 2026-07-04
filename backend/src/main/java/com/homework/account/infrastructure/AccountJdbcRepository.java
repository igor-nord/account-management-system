package com.homework.account.infrastructure;

import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.domain.LedgerCode;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface AccountJdbcRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByAccountId(Long accountId);

    List<Account> findByCustomerId(Long customerId);

    Optional<Account> findByCodeAndCurrency(LedgerCode code, Currency currency);
}

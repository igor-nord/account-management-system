package com.homework.transaction.infrastructure.repository;

import com.homework.transaction.domain.AccountTransaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface AccountTransactionJdbcRepository extends CrudRepository<AccountTransaction, Long> {

    List<AccountTransaction> findByAccountIdOrderByCreatedAtAscIdAsc(Long accountId);
}

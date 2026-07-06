package com.homework.transaction.repository;

import com.homework.transaction.domain.AccountTransaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface AccountTransactionDao extends CrudRepository<AccountTransaction, Long> {

    List<AccountTransaction> findByAccountIdOrderByCreatedAtAscIdAsc(Long accountId);

    List<AccountTransaction> findByTransactionIdOrderByIdAsc(String transactionId);
}

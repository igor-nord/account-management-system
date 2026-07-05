package com.homework.account.infrastructure.repository;

import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.domain.LedgerCode;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface AccountJdbcRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByAccountId(Long accountId);

    @Query("""
            SELECT a.* FROM account a
            JOIN customer c ON a.customer_id = c.id
            WHERE a.account_id = :accountId AND c.username = :username
            """)
    Optional<Account> findByAccountIdAndCustomerUsername(@Param("accountId") Long accountId,
                                                         @Param("username") String username);

    @Query("""
            SELECT a.* FROM account a
            JOIN customer c ON a.customer_id = c.id
            WHERE c.username = :username
            """)
    List<Account> findByCustomerUsername(@Param("username") String username);

    Optional<Account> findByCodeAndCurrency(LedgerCode code, Currency currency);
}

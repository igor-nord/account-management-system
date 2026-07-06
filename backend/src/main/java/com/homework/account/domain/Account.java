package com.homework.account.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

@Table("account")
public record Account(
        @Id Long id,
        @ReadOnlyProperty Long accountCode,
        Long customerId,
        AccountType accountType,
        LedgerCode ledgerCode,
        Currency currency,
        BigDecimal balance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static Account newCustomerAccount(Long customerId, Currency currency) {
        return new Account(null, null, customerId, AccountType.CUSTOMER, null, currency,
                BigDecimal.ZERO, null, null);
    }

    public Account withBalance(BigDecimal newBalance) {
        return new Account(id, accountCode, customerId, accountType, ledgerCode, currency, newBalance, createdAt, updatedAt);
    }
}

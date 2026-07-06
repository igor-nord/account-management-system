package com.homework.transaction.repository;

import com.homework.account.domain.Currency;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository adapter;

    private static AccountTransaction leg(String txnId, long accountCode, long counterparty,
                                          TransactionType type, String amount, Currency currency) {
        return new AccountTransaction(null, txnId, accountCode, counterparty, type,
                new BigDecimal(amount), currency, "Exchange USD->EUR", null);
    }

    @Test
    void savesLegAndAssignsIdAndTimestamp() {
        AccountTransaction saved = adapter.save(
                leg("TXN0000000001", 1000012L, 1000002L, TransactionType.DEBIT, "100.00", Currency.USD));

        assertNotNull(saved.id());
        assertNotNull(saved.createdAt());
        assertEquals(new BigDecimal("100.00"), saved.amount());
    }

    @Test
    void savesAllLegsAndFindsByAccount() {
        List<AccountTransaction> legs = List.of(
                leg("TXN0000000002", 1000012L, 1000002L, TransactionType.DEBIT, "100.00", Currency.USD),
                leg("TXN0000000002", 1000002L, 1000012L, TransactionType.CREDIT, "100.00", Currency.USD));

        adapter.saveAll(legs);

        List<AccountTransaction> forCustomerUsd = adapter.findByAccountCode(1000012L);
        assertEquals(1, forCustomerUsd.size());
        assertEquals(TransactionType.DEBIT, forCustomerUsd.getFirst().type());
    }
}

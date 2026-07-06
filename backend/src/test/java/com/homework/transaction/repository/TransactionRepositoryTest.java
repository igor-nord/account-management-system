package com.homework.transaction.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.homework.account.domain.Currency;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private static AccountTransaction leg(String txnId, long accountCode, long counterparty,
                                          TransactionType type, String amount, Currency currency) {
        return new AccountTransaction(null, txnId, accountCode, counterparty, type,
                new BigDecimal(amount), currency, "Exchange USD->EUR", null);
    }

    @Test
    void savesLegAndAssignsIdAndTimestamp() {
        AccountTransaction saved = transactionRepository.saveAll(
            Collections
                .singletonList(leg("TXN0000000001", 1000012L, 1000002L, TransactionType.DEBIT, "100.00", Currency.USD)))
            .getFirst();

        assertNotNull(saved.id());
        assertNotNull(saved.createdAt());
        assertEquals(new BigDecimal("100.00"), saved.amount());
    }

    @Test
    void savesAllLegsAndFindsByAccount() {
        List<AccountTransaction> legs = List.of(
                leg("TXN0000000002", 1000012L, 1000002L, TransactionType.DEBIT, "100.00", Currency.USD),
                leg("TXN0000000002", 1000002L, 1000012L, TransactionType.CREDIT, "100.00", Currency.USD));

        transactionRepository.saveAll(legs);

        List<AccountTransaction> forCustomerUsd = transactionRepository.findByAccountCode(1000012L);
        assertEquals(1, forCustomerUsd.size());
        assertEquals(TransactionType.DEBIT, forCustomerUsd.getFirst().type());
    }
}

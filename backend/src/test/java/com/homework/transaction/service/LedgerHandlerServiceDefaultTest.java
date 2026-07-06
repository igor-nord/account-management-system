package com.homework.transaction.service;

import com.homework.account.domain.Currency;
import com.homework.account.repository.AccountRepository;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class LedgerHandlerServiceDefaultTest {

    @Autowired
    private LedgerHandlerService ledgerHandler;
    @Autowired
    private AccountRepository accounts;

    @Test
    void postsBalancedLegsAndUpdatesBalances() {
        BigDecimal before = accounts.findByAccountCode(1000011L).orElseThrow().balance();
        List<AccountTransaction> legs = List.of(
                AccountTransaction.newLeg("TXN0000000010", 1000011L, 1000006L, TransactionType.CREDIT,
                        new BigDecimal("50.00"), Currency.EUR, "deposit"),
                AccountTransaction.newLeg("TXN0000000010", 1000006L, 1000011L, TransactionType.DEBIT,
                        new BigDecimal("50.00"), Currency.EUR, "deposit"));

        List<AccountTransaction> visible = ledgerHandler.handleAllTxLegs(legs, Set.of(1000011L));

        assertEquals(before.add(new BigDecimal("50.00")), accounts.findByAccountCode(1000011L).orElseThrow().balance());
        assertEquals(1, visible.size());
        assertEquals(1000011L, visible.getFirst().accountCode());
    }

    @Test
    void rejectsUnbalancedLegs() {
        List<AccountTransaction> legs = List.of(
                AccountTransaction.newLeg("TXN0000000011", 1000011L, 1000006L, TransactionType.CREDIT,
                        new BigDecimal("50.00"), Currency.EUR, "bad"));
        assertThrows(IllegalStateException.class, () -> ledgerHandler.handleAllTxLegs(legs, Set.of(1000011L)));
    }
}

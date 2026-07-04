package com.homework.transaction.usecase;

import com.homework.account.domain.Currency;
import com.homework.account.port.AccountRepository;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class LedgerWriterTest {

    @Autowired
    private LedgerWriter ledgerWriter;
    @Autowired
    private AccountRepository accounts;

    @Test
    void postsBalancedLegsAndUpdatesBalances() {
        BigDecimal before = accounts.findByAccountId(1000011L).orElseThrow().balance();
        List<AccountTransaction> legs = List.of(
                AccountTransaction.newLeg("TXN0000000010", 1000011L, 1000006L, TransactionType.CREDIT,
                        new BigDecimal("50.00"), Currency.EUR, "deposit"),
                AccountTransaction.newLeg("TXN0000000010", 1000006L, 1000011L, TransactionType.DEBIT,
                        new BigDecimal("50.00"), Currency.EUR, "deposit"));

        ledgerWriter.post(legs);

        assertEquals(before.add(new BigDecimal("50.00")), accounts.findByAccountId(1000011L).orElseThrow().balance());
    }

    @Test
    void rejectsUnbalancedLegs() {
        List<AccountTransaction> legs = List.of(
                AccountTransaction.newLeg("TXN0000000011", 1000011L, 1000006L, TransactionType.CREDIT,
                        new BigDecimal("50.00"), Currency.EUR, "bad"));
        assertThrows(IllegalStateException.class, () -> ledgerWriter.post(legs));
    }
}

package com.homework.history.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.homework.account.domain.Currency;
import com.homework.history.domain.BalancePoint;
import com.homework.history.domain.HistoryCursor;
import com.homework.history.domain.HistoryItem;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import com.homework.transaction.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AccountHistoryDaoTest {

    @Autowired
    private AccountHistoryDao historyDao;
    @Autowired
    private TransactionRepository transactions;

    private int seq = 0;

    private void leg(long accountCode, TransactionType type, String amount) {
        seq++;
        transactions.saveAll(Collections.singletonList(AccountTransaction.newLeg("T" + String.format("%012d", seq), accountCode, 1000006L,
                type, new BigDecimal(amount), Currency.EUR, "x")));
    }

    @Test
    void getBalanceSeriesIsRunningSumChronological() {
        leg(1000011L, TransactionType.CREDIT, "100.00");
        leg(1000011L, TransactionType.DEBIT, "30.00");

        List<BalancePoint> series = historyDao.getBalanceSeries(1000011L);

        assertEquals(2, series.size());
        assertEquals(new BigDecimal("100.00"), series.get(0).balance());
        assertEquals(new BigDecimal("70.00"), series.get(1).balance());
    }

    @Test
    void getHistoryPageKeysetReturnsNewestFirstAndWalks() {
        for (int i = 1; i <= 5; i++) {
            leg(1000012L, TransactionType.CREDIT, i + ".00");
        }

        List<HistoryItem> first = historyDao.getHistoryPage(1000012L, 2, null);
        assertEquals(2, first.size());
        assertEquals(new BigDecimal("5.00"), first.get(0).amount());
        assertEquals(new BigDecimal("4.00"), first.get(1).amount());

        HistoryItem last = first.get(1);
        List<HistoryItem> second = historyDao.getHistoryPage(1000012L, 2, new HistoryCursor(last.createdAt(), last.transactionId()));
        assertEquals(2, second.size());
        assertEquals(new BigDecimal("3.00"), second.get(0).amount());
        assertEquals(new BigDecimal("2.00"), second.get(1).amount());
    }
}

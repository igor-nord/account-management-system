package com.homework.reporting.service;

import com.homework.account.domain.Currency;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenPdfTransactionRendererTest {

    private final OpenPdfTransactionRenderer renderer = new OpenPdfTransactionRenderer();

    @Test
    void rendersPdfBytesStartingWithMagicHeader() {
        AccountTransaction leg = AccountTransaction.newLeg("TXN0000000001", 1000011L, 1000006L,
                TransactionType.CREDIT, new BigDecimal("25.00"), Currency.EUR, "Deposit");

        byte[] pdf = renderer.render("TXN0000000001", Instant.now(), List.of(leg));

        assertTrue(pdf.length > 0);
        assertTrue(pdf[0] == '%' && pdf[1] == 'P' && pdf[2] == 'D' && pdf[3] == 'F',
                "PDF must start with the %PDF magic header");
    }
}

package com.homework.exchange.service;

import com.homework.account.domain.Currency;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyExchangeServiceMockTest {

    private final CurrencyExchangeServiceMock service = new CurrencyExchangeServiceMock();

    @Test
    void identityConversionKeepsAmount() {
        assertEquals(new BigDecimal("100.00"), service.convert(new BigDecimal("100"), Currency.EUR, Currency.EUR));
    }

    @Test
    void inverseConversionRoundTripsApproximatelyToOriginal() {
        BigDecimal toUsd = service.convert(new BigDecimal("100"), Currency.EUR, Currency.USD);
        BigDecimal backToEur = service.convert(toUsd, Currency.USD, Currency.EUR);
        assertTrue(backToEur.subtract(new BigDecimal("100")).abs().compareTo(new BigDecimal("0.01")) <= 0);
    }

    @Test
    void convertsWithTwoDecimalScale() {
        BigDecimal result = service.convert(new BigDecimal("100"), Currency.EUR, Currency.USD);
        assertEquals(new BigDecimal("108.00"), result);
    }

    @Test
    void crossConversionUsesEurPivot() {
        assertEquals(new BigDecimal("1046.30"), service.convert(new BigDecimal("100"), Currency.USD, Currency.SEK));
    }

    @Test
    void nullAmountIsRejected() {
        assertThrows(NullPointerException.class, () -> service.convert(null, Currency.EUR, Currency.USD));
    }

    @Test
    void unsupportedPairIsRejected() {
        assertThrows(NullPointerException.class, () -> service.convert(new BigDecimal("100"), null, Currency.EUR));
    }
}

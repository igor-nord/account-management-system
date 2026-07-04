package com.homework.exchange.infrastructure.external;

import com.homework.account.domain.Currency;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExchangeRateServiceMockTest {

    private final ExchangeRateServiceMock service = new ExchangeRateServiceMock();

    @Test
    void identityRateIsOne() {
        assertEquals(0, BigDecimal.ONE.compareTo(service.rate(Currency.EUR, Currency.EUR)));
    }

    @Test
    void inverseRatesRoundTripApproximatelyToOne() {
        BigDecimal product = service.rate(Currency.EUR, Currency.USD)
                .multiply(service.rate(Currency.USD, Currency.EUR));
        assertTrue(product.subtract(BigDecimal.ONE).abs().compareTo(new BigDecimal("0.0001")) < 0);
    }

    @Test
    void convertsWithTwoDecimalScale() {
        BigDecimal result = service.convert(new BigDecimal("100"), Currency.EUR, Currency.USD);
        assertEquals(new BigDecimal("108.00"), result);
    }

    @Test
    void crossRateUsesEurPivot() {
        BigDecimal expected = new BigDecimal("11.30").divide(new BigDecimal("1.08"), 6, RoundingMode.HALF_UP);
        assertEquals(0, expected.compareTo(service.rate(Currency.USD, Currency.SEK)));
    }

    @Test
    void unsupportedPairIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.rate(null, Currency.EUR));
    }
}

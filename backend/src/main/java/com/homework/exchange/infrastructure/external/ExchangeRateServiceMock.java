package com.homework.exchange.infrastructure.external;

import com.homework.account.domain.Currency;
import com.homework.exchange.port.ExchangeRateService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class ExchangeRateServiceMock implements ExchangeRateService {

    private static final Map<Currency, BigDecimal> EUR_RATES = Map.of(
            Currency.EUR, new BigDecimal("1.00"),
            Currency.USD, new BigDecimal("1.08"),
            Currency.SEK, new BigDecimal("11.30"),
            Currency.GBP, new BigDecimal("0.85"),
            Currency.VND, new BigDecimal("27000.00"));

    private static final int RATE_SCALE = 6;
    private static final int MONEY_SCALE = 4;

    @Override
    public BigDecimal rate(Currency from, Currency to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Currencies must not be null");
        }
        BigDecimal fromRate = EUR_RATES.get(from);
        BigDecimal toRate = EUR_RATES.get(to);
        if (fromRate == null || toRate == null) {
            throw new IllegalArgumentException("Unsupported currency pair: " + from + " -> " + to);
        }
        return toRate.divide(fromRate, RATE_SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        return amount.multiply(rate(from, to)).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}

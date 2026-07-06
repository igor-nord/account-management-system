package com.homework.exchange.service;

import com.homework.account.domain.Currency;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CurrencyExchangeServiceMock implements CurrencyExchangeService {

    private static final Map<Currency, BigDecimal> EUR_RATES = Map.of(
            Currency.EUR, new BigDecimal("1.00"),
            Currency.USD, new BigDecimal("1.08"),
            Currency.SEK, new BigDecimal("11.30"),
            Currency.GBP, new BigDecimal("0.85"),
            Currency.VND, new BigDecimal("27000.00"));

    private static final int RATE_SCALE = 6;
    private static final int CONVERSION_SCALE = 2;

    private BigDecimal getPairRate(Currency currencyFrom, Currency currencyTo) {
        BigDecimal sourceRate = EUR_RATES.get(currencyFrom);
        BigDecimal targetRate = EUR_RATES.get(currencyTo);
        return targetRate.divide(sourceRate, RATE_SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal convert(BigDecimal amount, Currency currencyFrom, Currency currencyTo) {
        return amount.multiply(getPairRate(currencyFrom, currencyTo)).setScale(CONVERSION_SCALE, RoundingMode.HALF_UP);
    }
}

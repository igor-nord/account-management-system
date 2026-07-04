package com.homework.exchange.port;

import com.homework.account.domain.Currency;

import java.math.BigDecimal;

public interface ExchangeRateService {

    BigDecimal rate(Currency from, Currency to);

    BigDecimal convert(BigDecimal amount, Currency from, Currency to);
}

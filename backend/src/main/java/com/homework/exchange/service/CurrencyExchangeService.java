package com.homework.exchange.service;

import com.homework.account.domain.Currency;

import java.math.BigDecimal;

public interface CurrencyExchangeService {

    BigDecimal convert(BigDecimal amount, Currency currencyFrom, Currency currencyTo);
}

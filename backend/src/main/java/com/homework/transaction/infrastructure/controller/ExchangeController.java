package com.homework.transaction.infrastructure.controller;

import com.homework.common.web.CurrentUsername;
import com.homework.transaction.port.ExchangeUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exchange")
class ExchangeController {

    private final ExchangeUseCase exchange;

    ExchangeController(ExchangeUseCase exchange) {
        this.exchange = exchange;
    }

    @PostMapping
    TransactionResponse exchange(@CurrentUsername String username,
                                 @RequestHeader("X-Source-Account-Id") Long sourceAccountId,
                                 @RequestHeader("X-Target-Account-Id") Long targetAccountId,
                                 @Valid @RequestBody AmountRequest request) {
        return TransactionResponse.of(
                exchange.exchange(username, sourceAccountId, targetAccountId, request.amount()));
    }
}

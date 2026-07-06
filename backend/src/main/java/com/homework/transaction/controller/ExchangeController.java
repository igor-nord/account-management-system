package com.homework.transaction.controller;

import com.homework.common.web.CurrentUsername;
import com.homework.transaction.service.ExchangeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exchange")
class ExchangeController {

    private final ExchangeService exchangeService;

    ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PostMapping
    TransactionResponse exchange(@CurrentUsername String username,
                                 @RequestHeader("X-Source-Account-Code") Long sourceAccountCode,
                                 @RequestHeader("X-Target-Account-Code") Long targetAccountCode,
                                 @Valid @RequestBody AmountRequest request) {
        return TransactionResponse.of(
                exchangeService.exchange(username, sourceAccountCode, targetAccountCode, request.amount()));
    }
}

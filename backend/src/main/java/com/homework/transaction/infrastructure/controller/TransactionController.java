package com.homework.transaction.infrastructure.controller;

import com.homework.transaction.port.GetTransactionUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction")
class TransactionController {

    private final GetTransactionUseCase getTransaction;

    TransactionController(GetTransactionUseCase getTransaction) {
        this.getTransaction = getTransaction;
    }

    @GetMapping
    TransactionResponse get(@RequestHeader("X-Customer-Id") Long customerId,
                            @RequestHeader("X-Transaction-Id") String transactionId) {
        return TransactionResponse.of(getTransaction.byTransactionId(customerId, transactionId));
    }
}

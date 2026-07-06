package com.homework.transaction.controller;

import com.homework.common.web.CurrentUsername;
import com.homework.transaction.service.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction")
class TransactionController {

    private final TransactionService getTransaction;

    TransactionController(TransactionService getTransaction) {
        this.getTransaction = getTransaction;
    }

    @GetMapping
    TransactionResponse get(@CurrentUsername String username,
                            @RequestHeader("X-Transaction-Id") String transactionId) {
        return TransactionResponse.of(getTransaction.getTransaction(username, transactionId));
    }
}

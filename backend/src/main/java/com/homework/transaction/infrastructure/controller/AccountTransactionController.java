package com.homework.transaction.infrastructure.controller;

import com.homework.common.web.CurrentUsername;
import com.homework.transaction.port.CreditAccountUseCase;
import com.homework.transaction.port.DebitAccountUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
class AccountTransactionController {

    private final CreditAccountUseCase creditAccount;
    private final DebitAccountUseCase debitAccount;

    AccountTransactionController(CreditAccountUseCase creditAccount, DebitAccountUseCase debitAccount) {
        this.creditAccount = creditAccount;
        this.debitAccount = debitAccount;
    }

    @PostMapping("/credit")
    TransactionResponse credit(@CurrentUsername String username,
                               @RequestHeader("X-Account-Id") Long accountId,
                               @Valid @RequestBody AmountRequest request) {
        return TransactionResponse.of(
                creditAccount.credit(username, accountId, request.amount(), request.description()));
    }

    @PostMapping("/debit")
    TransactionResponse debit(@CurrentUsername String username,
                              @RequestHeader("X-Account-Id") Long accountId,
                              @Valid @RequestBody AmountRequest request) {
        return TransactionResponse.of(
                debitAccount.debit(username, accountId, request.amount(), request.description()));
    }
}

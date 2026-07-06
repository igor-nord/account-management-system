package com.homework.transaction.controller;

import com.homework.common.web.CurrentUsername;
import com.homework.transaction.service.CreditAccountService;
import com.homework.transaction.service.DebitAccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
class AccountTransactionController {

    private final CreditAccountService creditAccount;
    private final DebitAccountService debitAccount;

    AccountTransactionController(CreditAccountService creditAccount, DebitAccountService debitAccount) {
        this.creditAccount = creditAccount;
        this.debitAccount = debitAccount;
    }

    @PostMapping("/credit")
    TransactionResponse credit(@CurrentUsername String username,
                               @RequestHeader("X-Account-Code") Long accountCode,
                               @Valid @RequestBody AmountRequest request) {
        return TransactionResponse.of(
                creditAccount.credit(username, accountCode, request.amount(), request.description()));
    }

    @PostMapping("/debit")
    TransactionResponse debit(@CurrentUsername String username,
                              @RequestHeader("X-Account-Code") Long accountCode,
                              @Valid @RequestBody AmountRequest request) {
        return TransactionResponse.of(
                debitAccount.debit(username, accountCode, request.amount(), request.description()));
    }
}

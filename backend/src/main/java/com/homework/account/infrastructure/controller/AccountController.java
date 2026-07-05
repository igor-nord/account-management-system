package com.homework.account.infrastructure.controller;

import com.homework.account.usecase.AccountAccess;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
class AccountController {

    private final AccountAccess accountAccess;

    AccountController(AccountAccess accountAccess) {
        this.accountAccess = accountAccess;
    }

    @GetMapping("/accounts")
    List<AccountSummary> list(@RequestHeader("X-Customer-Id") Long customerId) {
        return accountAccess.ownedAccounts(customerId).stream().map(AccountSummary::of).toList();
    }

    @GetMapping("/account")
    AccountSummary get(@RequestHeader("X-Customer-Id") Long customerId,
                       @RequestHeader("X-Account-Id") Long accountId) {
        return AccountSummary.of(accountAccess.requireOwned(customerId, accountId));
    }
}

package com.homework.account.controller;

import com.homework.account.service.AccountService;
import com.homework.common.web.CurrentUsername;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
class AccountController {

    private final AccountService accountService;

    AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    List<AccountSummary> list(@CurrentUsername String username) {
        return accountService.ownedAccounts(username).stream().map(AccountSummary::of).toList();
    }

    @GetMapping("/account")
    AccountSummary get(@CurrentUsername String username,
                       @RequestHeader("X-Account-Code") Long accountCode) {
        return AccountSummary.of(accountService.requireOwned(username, accountCode));
    }
}

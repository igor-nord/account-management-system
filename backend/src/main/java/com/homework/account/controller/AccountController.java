package com.homework.account.controller;

import com.homework.account.service.AccountAccessService;
import com.homework.common.web.CurrentUsername;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
class AccountController {

    private final AccountAccessService accountAccess;

    AccountController(AccountAccessService accountAccess) {
        this.accountAccess = accountAccess;
    }

    @GetMapping("/accounts")
    List<AccountSummary> list(@CurrentUsername String username) {
        return accountAccess.ownedAccounts(username).stream().map(AccountSummary::of).toList();
    }

    @GetMapping("/account")
    AccountSummary get(@CurrentUsername String username,
                       @RequestHeader("X-Account-Id") Long accountId) {
        return AccountSummary.of(accountAccess.requireOwned(username, accountId));
    }
}

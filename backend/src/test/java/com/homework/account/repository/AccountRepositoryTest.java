package com.homework.account.repository;

import com.homework.account.domain.Account;
import com.homework.account.domain.Currency;
import com.homework.account.domain.LedgerCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class AccountRepositoryTest {

    @Autowired
    private AccountRepository adapter;

    @Test
    void savesNewAccountAndAssignsBusinessCode() {
        Account saved = adapter.save(Account.newCustomerAccount(1L, Currency.GBP));

        assertNotNull(saved.id(), "technical id assigned");
        assertNotNull(saved.createdAt());
        assertNotNull(saved.updatedAt());

        Account reloaded = adapter.findByCustomerUsername("demo").stream()
                .filter(a -> a.currency() == Currency.GBP)
                .findFirst().orElseThrow();
        assertNotNull(reloaded.accountCode(), "business account_code assigned by the DB sequence");
        assertTrue(reloaded.accountCode() >= 1000100L, "business code drawn from the sequence range");
    }

    @Test
    void findsSeededLedgerAccountByCodeAndCurrency() {
        Optional<Account> fx = adapter.findByLedgerCodeAndCurrency(LedgerCode.FX_CLEARING, Currency.USD);
        assertTrue(fx.isPresent());
        assertEquals(1000002L, fx.get().accountCode());
    }

    @Test
    void findsSeededCustomerAccounts() {
        assertEquals(3, adapter.findByCustomerUsername("demo").size());
    }
}

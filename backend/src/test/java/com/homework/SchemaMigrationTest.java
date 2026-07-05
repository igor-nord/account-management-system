package com.homework;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class SchemaMigrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void tablesAndSeedsExist() {
        Integer customers = jdbc.queryForObject("SELECT COUNT(*) FROM customer", Integer.class);
        assertEquals(3, customers);

        Integer accounts = jdbc.queryForObject("SELECT COUNT(*) FROM account", Integer.class);
        assertEquals(17, accounts);

        Long fxClearingUsd = jdbc.queryForObject(
                "SELECT account_id FROM account WHERE code = 'FX_CLEARING' AND currency = 'USD'", Long.class);
        assertEquals(1000002L, fxClearingUsd);

        Integer transactions = jdbc.queryForObject("SELECT COUNT(*) FROM account_transaction", Integer.class);
        assertEquals(0, transactions);
    }
}

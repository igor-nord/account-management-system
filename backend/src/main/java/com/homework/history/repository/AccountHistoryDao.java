package com.homework.history.repository;

import com.homework.account.domain.Currency;
import com.homework.history.domain.BalancePoint;
import com.homework.history.domain.HistoryCursor;
import com.homework.history.domain.HistoryItem;
import com.homework.transaction.domain.TransactionType;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AccountHistoryDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AccountHistoryDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<HistoryItem> getHistoryPage(Long accountCode, int limit, HistoryCursor cursor) {
        String sql = """
                SELECT transaction_id, type, amount, currency, description, created_at
                FROM account_transaction
                WHERE account_code = :accountCode
                  AND (:cursorCreatedAt IS NULL
                       OR created_at < :cursorCreatedAt
                       OR (created_at = :cursorCreatedAt AND transaction_id < :cursorTxnId))
                ORDER BY created_at DESC, transaction_id DESC
                LIMIT :limit
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("accountCode", accountCode)
                .addValue("cursorCreatedAt", cursor == null ? null : Timestamp.valueOf(cursor.createdAt()))
                .addValue("cursorTxnId", cursor == null ? null : cursor.transactionId())
                .addValue("limit", limit);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new HistoryItem(
                rs.getString("transaction_id"),
                TransactionType.valueOf(rs.getString("type")),
                rs.getBigDecimal("amount"),
                Currency.valueOf(rs.getString("currency")),
                rs.getString("description"),
                rs.getTimestamp("created_at").toLocalDateTime()));
    }

    public List<BalancePoint> getBalanceSeries(Long accountCode) {
        String sql = """
                SELECT created_at,
                       SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE -amount END)
                           OVER (ORDER BY created_at ASC, id ASC) AS balance
                FROM account_transaction
                WHERE account_code = :accountCode
                ORDER BY created_at ASC, id ASC
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("accountCode", accountCode);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new BalancePoint(
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBigDecimal("balance")));
    }
}

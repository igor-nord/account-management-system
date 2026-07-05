package com.homework.reporting.port;

import com.homework.transaction.domain.AccountTransaction;

import java.time.Instant;
import java.util.List;

public interface TransactionPdfRenderer {

    byte[] render(String transactionId, Instant createdAt, List<AccountTransaction> legs);
}

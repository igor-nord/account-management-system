package com.homework.reporting.port;

public interface ExportTransactionPdfUseCase {

    byte[] export(Long customerId, String transactionId);
}

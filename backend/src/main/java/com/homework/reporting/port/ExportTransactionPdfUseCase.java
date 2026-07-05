package com.homework.reporting.port;

public interface ExportTransactionPdfUseCase {

    byte[] export(String username, String transactionId);
}

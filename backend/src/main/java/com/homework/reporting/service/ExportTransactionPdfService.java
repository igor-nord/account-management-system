package com.homework.reporting.service;

public interface ExportTransactionPdfService {

    byte[] export(String username, String transactionId);
}

package com.homework.reporting.service;

public interface PdfService {

    byte[] createPdfForTransaction(String username, String transactionId);
}

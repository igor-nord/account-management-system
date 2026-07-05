package com.homework.reporting.infrastructure.controller;

import com.homework.reporting.port.ExportTransactionPdfUseCase;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction")
class ReportController {

    private final ExportTransactionPdfUseCase exportPdf;

    ReportController(ExportTransactionPdfUseCase exportPdf) {
        this.exportPdf = exportPdf;
    }

    @GetMapping("/pdf")
    ResponseEntity<byte[]> pdf(@RequestHeader("X-Customer-Id") Long customerId,
                               @RequestHeader("X-Transaction-Id") String transactionId) {
        byte[] pdf = exportPdf.export(customerId, transactionId);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename("transaction-" + transactionId + ".pdf").build();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", disposition.toString())
                .body(pdf);
    }
}

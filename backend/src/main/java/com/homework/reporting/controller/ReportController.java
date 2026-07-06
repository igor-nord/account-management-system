package com.homework.reporting.controller;

import com.homework.common.web.CurrentUsername;
import com.homework.reporting.service.PdfService;
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

    private final PdfService pdfService;

    ReportController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/pdf")
    ResponseEntity<byte[]> generatePdfForTransaction(
        @CurrentUsername String username,
        @RequestHeader("X-Transaction-Id") String transactionId) {

        byte[] pdf = pdfService.createPdfForTransaction(username, transactionId);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename("transaction-" + transactionId + ".pdf").build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", disposition.toString())
                .body(pdf);
    }
}

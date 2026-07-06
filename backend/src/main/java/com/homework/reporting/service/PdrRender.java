package com.homework.reporting.service;

import com.homework.transaction.domain.AccountTransaction;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class PdrRender {

    public byte[] render(String transactionId, LocalDateTime createdAt, List<AccountTransaction> legs) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Transaction Summary"));
            document.add(new Paragraph("Transaction ID: " + transactionId));
            document.add(new Paragraph("Date: " + createdAt));
            document.add(legTable(legs));
            document.close();
        } catch (DocumentException e) {
            throw new IllegalStateException("Failed to render transaction PDF", e);
        }
        return out.toByteArray();
    }

    private static PdfPTable legTable(List<AccountTransaction> legs) {
        PdfPTable table = new PdfPTable(6);
        table.addCell("Account");
        table.addCell("Counterparty");
        table.addCell("Type");
        table.addCell("Amount");
        table.addCell("Currency");
        table.addCell("Description");
        for (AccountTransaction leg : legs) {
            table.addCell(String.valueOf(leg.accountCode()));
            table.addCell(String.valueOf(leg.counterpartyAccountCode()));
            table.addCell(leg.type().name());
            table.addCell(leg.amount().toPlainString());
            table.addCell(leg.currency().name());
            table.addCell(leg.description());
        }
        return table;
    }
}

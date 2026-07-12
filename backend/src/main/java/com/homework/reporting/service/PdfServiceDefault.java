package com.homework.reporting.service;

import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.service.TransactionService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PdfServiceDefault implements PdfService {

    private final TransactionService transactionService;
    private final PdfRender   renderer;

    public PdfServiceDefault(TransactionService getTransaction, PdfRender   renderer) {
        this.transactionService = getTransaction;
        this.renderer = renderer;
    }

    @Override
    public byte[] createPdfForTransaction(String username, String transactionId) {
        List<AccountTransaction> legs = transactionService.getTransaction(username, transactionId);
        AccountTransaction first = legs.getFirst();
        return renderer.render(first.transactionId(), first.createdAt(), legs);
    }
}

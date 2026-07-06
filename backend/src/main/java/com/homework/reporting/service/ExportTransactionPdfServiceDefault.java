package com.homework.reporting.service;

import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.service.GetTransactionService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExportTransactionPdfServiceDefault implements ExportTransactionPdfService {

    private final GetTransactionService getTransaction;
    private final OpenPdfTransactionRenderer renderer;

    public ExportTransactionPdfServiceDefault(GetTransactionService getTransaction, OpenPdfTransactionRenderer renderer) {
        this.getTransaction = getTransaction;
        this.renderer = renderer;
    }

    @Override
    public byte[] export(String username, String transactionId) {
        List<AccountTransaction> legs = getTransaction.byTransactionId(username, transactionId);
        AccountTransaction first = legs.getFirst();
        return renderer.render(first.transactionId(), first.createdAt(), legs);
    }
}

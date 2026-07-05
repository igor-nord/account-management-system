package com.homework.reporting.usecase;

import com.homework.reporting.port.ExportTransactionPdfUseCase;
import com.homework.reporting.port.TransactionPdfRenderer;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.port.GetTransactionUseCase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExportTransactionPdfService implements ExportTransactionPdfUseCase {

    private final GetTransactionUseCase getTransaction;
    private final TransactionPdfRenderer renderer;

    public ExportTransactionPdfService(GetTransactionUseCase getTransaction, TransactionPdfRenderer renderer) {
        this.getTransaction = getTransaction;
        this.renderer = renderer;
    }

    @Override
    public byte[] export(Long customerId, String transactionId) {
        List<AccountTransaction> legs = getTransaction.byTransactionId(customerId, transactionId);
        AccountTransaction first = legs.getFirst();
        return renderer.render(first.transactionId(), first.createdAt(), legs);
    }
}

package com.homework.transaction.infrastructure.id;

import com.github.f4b6a3.tsid.TsidCreator;
import com.homework.transaction.port.TransactionIdGenerator;
import org.springframework.stereotype.Component;

@Component
public class TsidTransactionIdGenerator implements TransactionIdGenerator {

    @Override
    public String newTransactionId() {
        return TsidCreator.getTsid().toString();
    }
}

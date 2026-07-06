package com.homework.transaction.service;

import com.github.f4b6a3.tsid.TsidCreator;
import org.springframework.stereotype.Component;

@Component
public class TsidTransactionIdGenerator {

    public String newTransactionId() {
        return TsidCreator.getTsid().toString();
    }
}

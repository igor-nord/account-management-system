package com.homework.transaction.port;

public interface ExternalLoggingPort {

    void logBeforeDebit(String transactionId);
}

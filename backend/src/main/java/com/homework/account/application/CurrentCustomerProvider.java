package com.homework.account.application;

import org.springframework.stereotype.Component;

@Component
public class CurrentCustomerProvider {

    private static final long DEMO_CUSTOMER_ID = 1L;

    public Long currentCustomerId() {
        return DEMO_CUSTOMER_ID;
    }
}

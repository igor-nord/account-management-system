package com.homework.customer.infrastructure.controller;

import com.homework.customer.domain.Customer;

public record CustomerResponse(String username) {

    public static CustomerResponse of(Customer customer) {
        return new CustomerResponse(customer.username());
    }
}

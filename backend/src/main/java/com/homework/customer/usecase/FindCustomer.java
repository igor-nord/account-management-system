package com.homework.customer.usecase;

import com.homework.customer.domain.Customer;
import com.homework.customer.port.CustomerRepository;
import org.springframework.stereotype.Component;

@Component
public class FindCustomer {

    private final CustomerRepository customers;

    public FindCustomer(CustomerRepository customers) {
        this.customers = customers;
    }

    public Customer byUsername(String username) {
        return customers.findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException(username));
    }
}

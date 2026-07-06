package com.homework.customer.service;

import com.homework.customer.exception.CustomerNotFoundException;
import com.homework.customer.domain.Customer;
import com.homework.customer.repository.CustomerRepository;
import org.springframework.stereotype.Component;

@Component
public class CustomerServiceDefault implements CustomerService {

    private final CustomerRepository customers;

    public CustomerServiceDefault(CustomerRepository customers) {
        this.customers = customers;
    }

    @Override
    public Customer byUsername(String username) {
        return customers.findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException(username));
    }
}

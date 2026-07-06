package com.homework.customer.service;

import com.homework.customer.exception.CustomerNotFoundException;
import com.homework.customer.domain.Customer;
import com.homework.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceDefault implements CustomerService {

    private final CustomerRepository repository;

    public CustomerServiceDefault(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Customer getCustomer(String username) {
        return repository.getCustomer(username)
                .orElseThrow(() -> new CustomerNotFoundException(username));
    }
}

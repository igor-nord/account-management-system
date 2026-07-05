package com.homework.customer.infrastructure.repository;

import com.homework.customer.domain.Customer;
import com.homework.customer.port.CustomerRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJdbcRepository jdbc;

    public CustomerRepositoryAdapter(CustomerJdbcRepository jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Customer> findByUsername(String username) {
        return jdbc.findByUsername(username);
    }
}

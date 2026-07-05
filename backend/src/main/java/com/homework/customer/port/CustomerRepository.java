package com.homework.customer.port;

import com.homework.customer.domain.Customer;

import java.util.Optional;

public interface CustomerRepository {

    Optional<Customer> findByUsername(String username);
}

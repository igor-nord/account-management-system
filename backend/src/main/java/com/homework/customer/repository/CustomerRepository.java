package com.homework.customer.repository;

import com.homework.customer.domain.Customer;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerRepository {

    private final CustomerDao customerDao;

    public CustomerRepository(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public Optional<Customer> findByUsername(String username) {
        return customerDao.findByUsername(username);
    }
}

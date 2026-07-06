package com.homework.customer.repository;

import com.homework.customer.domain.Customer;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepository {

    private final CustomerDao customerDao;

    public CustomerRepository(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public Optional<Customer> getCustomer(String username) {
        return customerDao.findByUsername(username);
    }
}

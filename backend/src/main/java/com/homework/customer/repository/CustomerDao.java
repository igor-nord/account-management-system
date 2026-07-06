package com.homework.customer.repository;

import com.homework.customer.domain.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface CustomerDao extends CrudRepository<Customer, Long> {

    Optional<Customer> findByUsername(String username);
}

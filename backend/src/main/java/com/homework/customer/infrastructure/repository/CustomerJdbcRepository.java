package com.homework.customer.infrastructure.repository;

import com.homework.customer.domain.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface CustomerJdbcRepository extends CrudRepository<Customer, Long> {

    Optional<Customer> findByUsername(String username);
}

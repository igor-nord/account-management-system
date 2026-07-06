package com.homework.customer.controller;

import com.homework.common.web.CurrentUsername;
import com.homework.customer.service.CustomerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
class CustomerController {

    private final CustomerService findCustomer;

    CustomerController(CustomerService findCustomer) {
        this.findCustomer = findCustomer;
    }

    @GetMapping
    CustomerResponse current(@CurrentUsername String username) {
        return new CustomerResponse(findCustomer.byUsername(username).username());
    }
}

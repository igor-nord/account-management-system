package com.homework.customer.controller;

import com.homework.common.web.CurrentUsername;
import com.homework.customer.service.CustomerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
class CustomerController {

    private final CustomerService customerService;

    CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    CustomerResponse getCustomer(@CurrentUsername String username) {
        return new CustomerResponse(customerService.getCustomer(username).username());
    }
}

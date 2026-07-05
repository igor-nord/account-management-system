package com.homework.customer.infrastructure.controller;

import com.homework.customer.usecase.FindCustomer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
class CustomerController {

    private final FindCustomer findCustomer;

    CustomerController(FindCustomer findCustomer) {
        this.findCustomer = findCustomer;
    }

    @GetMapping
    CustomerResponse byUsername(@RequestParam String username) {
        return CustomerResponse.of(findCustomer.byUsername(username));
    }
}

package com.homework.customer.infrastructure.controller;

import com.homework.common.web.CurrentUsername;
import com.homework.customer.usecase.FindCustomer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
class CustomerController {

    private final FindCustomer findCustomer;

    CustomerController(FindCustomer findCustomer) {
        this.findCustomer = findCustomer;
    }

    @GetMapping
    CustomerResponse current(@CurrentUsername String username) {
        return new CustomerResponse(findCustomer.byUsername(username).username());
    }
}

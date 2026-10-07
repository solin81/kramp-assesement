package com.kramp.customer.controller;

import com.kramp.customer.dto.Customer;
import com.kramp.customer.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Customer> getAllCustomers(@RequestParam(name = "marketCode", defaultValue = "en-EN") String marketCode) {
        return service.getAllCustomers(marketCode);
    }

    @GetMapping("/{customerId}")
    public Customer byCustomerId(@PathVariable("customerId") String customerId,
                                 @RequestParam(name = "marketCode", defaultValue = "en-EN") String marketCode) {
        return service.byCustomerId(customerId, marketCode);
    }
}

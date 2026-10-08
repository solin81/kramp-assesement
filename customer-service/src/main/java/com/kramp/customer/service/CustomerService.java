package com.kramp.customer.service;

import com.kramp.customer.dto.Customer;

import java.util.Collection;

/** Contract for retrieving customer data from the configured customer source. */
public interface CustomerService {
    Collection<Customer> getAllCustomers(String marketCode);

    Customer byCustomerId(String customerId, String marketCode);
}

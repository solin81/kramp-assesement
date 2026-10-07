package com.kramp.customer;

import com.kramp.customer.service.CustomerService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerServiceTest {
    @Test
    void returnsSeededCustomer() {
        assertEquals("PROFESSIONAL", new CustomerService().byCustomerId("C-100", "en-EN").segment());
    }
}

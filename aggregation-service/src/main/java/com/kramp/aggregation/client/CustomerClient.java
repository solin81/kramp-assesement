package com.kramp.aggregation.client;

import com.kramp.aggregation.dto.Customer;

/**
 * Port used by aggregation to retrieve customer data.
 */
public interface CustomerClient {
    ServiceResult<Customer> getCustomer(String customerId, String marketCode);
}

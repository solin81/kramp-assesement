package com.kramp.aggregation.client;

import com.kramp.aggregation.dto.Price;

/**
 * Port used by aggregation to retrieve pricing data.
 */
public interface PricingClient {
    ServiceResult<Price> getPrice(String productId, String marketCode, String customerId);
}

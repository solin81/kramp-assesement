package com.kramp.pricing.service;

import com.kramp.pricing.dto.Price;

import java.util.Collection;

/** Contract for retrieving prices from the configured pricing data source. */
public interface PricingService {
    Price byProductId(String productId, String marketCode, String customerId);

    Collection<Price> getAllPrices(String marketCode, String customerId);
}

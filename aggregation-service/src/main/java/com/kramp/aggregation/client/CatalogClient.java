package com.kramp.aggregation.client;

import com.kramp.aggregation.dto.Product;

/**
 * Port used by aggregation to retrieve catalog data.
 */
public interface CatalogClient {
    ServiceResult<Product> getProduct(String productId, String marketCode);
}

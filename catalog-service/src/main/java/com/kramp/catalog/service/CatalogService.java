package com.kramp.catalog.service;

import com.kramp.catalog.dto.Product;

import java.util.Collection;

/**
 * Contract for retrieving catalog data. Implementations may use in-memory data,
 * a service-owned database, or an external PIM without changing the HTTP API.
 */
public interface CatalogService {
    Collection<Product> getAllProducts(String marketCode);

    Product byProductId(String productId, String marketCode);
}

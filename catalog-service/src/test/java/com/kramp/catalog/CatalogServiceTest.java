package com.kramp.catalog;

import com.kramp.catalog.service.CatalogService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CatalogServiceTest {
    @Test
    void returnsSeededProduct() {
        var product = new CatalogService().byProductId("P-100", "en-EN");
        assertEquals("Hydraulic Filter", product.name());
    }
}

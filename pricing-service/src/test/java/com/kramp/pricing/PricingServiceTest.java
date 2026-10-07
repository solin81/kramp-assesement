package com.kramp.pricing;

import com.kramp.pricing.service.PricingService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingServiceTest {
    @Test
    void returnsSeededPrice() {
        var pricingService = new PricingService();
        var englishPrice = pricingService.byProductId("P-100", "en-EN", "C-100");
        var dutchPrice = pricingService.byProductId("P-100", "nl-NL", "C-100");

        assertEquals("29.95", englishPrice.basePrice().toPlainString());
        assertEquals(englishPrice.basePrice(), dutchPrice.basePrice());
        assertEquals("2.99", englishPrice.customerDiscount().toPlainString());
    }
}

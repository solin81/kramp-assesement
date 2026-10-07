package com.kramp.availability;

import com.kramp.availability.service.AvailabilityService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AvailabilityServiceTest {
    @Test
    void returnsStockLevel() {
        assertEquals(42, new AvailabilityService().byProductId("P-100", "en-EN").stockLevel());
    }
}

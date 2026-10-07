package com.kramp.aggregation;

import com.kramp.aggregation.service.AggregationService;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AggregationServiceTest {
    @Test
    void rejectsUnsupportedMarketBeforeCallingServices() {
        assertThrows(ResponseStatusException.class, () -> new AggregationService(null).getProduct("P-100", "fr-FR", null));
    }
}

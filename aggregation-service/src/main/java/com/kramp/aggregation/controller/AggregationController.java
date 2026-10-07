package com.kramp.aggregation.controller;

import com.kramp.aggregation.dto.AggregatedProduct;
import com.kramp.aggregation.service.AggregationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aggregated")
public class AggregationController {
    private final AggregationService service;

    public AggregationController(AggregationService service) {
        this.service = service;
    }

    @GetMapping("/products/{productId}")
    public AggregatedProduct getProduct(@PathVariable("productId") String productId,
                                        @RequestParam(name = "marketCode", defaultValue = "en-EN") String marketCode,
                                        @RequestParam(name = "customerId", required = false) String customerId) {
        return service.getProduct(productId, marketCode, customerId);
    }
}

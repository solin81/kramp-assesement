package com.kramp.pricing.controller;

import com.kramp.pricing.dto.Price;
import com.kramp.pricing.service.PricingService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/prices")
public class PricingController {
    private final PricingService service;

    public PricingController(PricingService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Price> getAllPrices(@RequestParam(name = "marketCode", defaultValue = "en-EN") String marketCode,
                                          @RequestParam(name = "customerId", required = false) String customerId) {
        return service.getAllPrices(marketCode, customerId);
    }

    @GetMapping("/{productId}")
    public Price byProductId(@PathVariable("productId") String productId,
                             @RequestParam(name = "marketCode", defaultValue = "en-EN") String marketCode,
                             @RequestParam(name = "customerId", required = false) String customerId) {
        return service.byProductId(productId, marketCode, customerId);
    }
}

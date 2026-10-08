package com.kramp.pricing.service;

import com.kramp.pricing.dto.Price;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class PricingService {
    private final ServiceBehaviour behaviour;
    private final Map<String, BigDecimal> basePrices = Map.of(
            "P-100", new BigDecimal("29.95"),
            "P-200", new BigDecimal("12.50"));
    private final Map<String, BigDecimal> customerDiscounts = Map.of(
            "C-100", new BigDecimal("0.10"),
            "C-200", new BigDecimal("0.15"));

    @Autowired
    public PricingService(ServiceBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    public PricingService() {
        this.behaviour = null;
    }

    public Price byProductId(String productId, String marketCode, String customerId) {
        simulate();
        validateMarket(marketCode);
        return calculatePrice(productId, customerId);
    }

    public Collection<Price> getAllPrices(String marketCode, String customerId) {
        simulate();
        validateMarket(marketCode);
        return basePrices.keySet().stream().map(productId -> calculatePrice(productId, customerId)).toList();
    }

    private Price calculatePrice(String productId, String customerId) {
        var basePrice = basePrices.get(productId);
        if (basePrice == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Price not found: " + productId);
        var discountForCustomer = discountFor(customerId);
        var finalPrice = basePrice.multiply(BigDecimal.ONE.subtract(discountForCustomer)).setScale(2, RoundingMode.HALF_UP);
        return new Price(productId, basePrice, basePrice.subtract(finalPrice), finalPrice);
    }

    private void simulate() {
        if (behaviour != null) behaviour.simulate();
    }

    private BigDecimal discountFor(String customerId) {
        return customerId == null
                ? BigDecimal.ZERO
                : customerDiscounts.getOrDefault(customerId, BigDecimal.ZERO);
    }

    private void validateMarket(String marketCode) {
        if (!List.of("en-EN", "nl-NL", "de-DE").contains(marketCode))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported market code: " + marketCode);
    }
}

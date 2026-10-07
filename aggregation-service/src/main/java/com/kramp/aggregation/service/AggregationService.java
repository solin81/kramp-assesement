package com.kramp.aggregation.service;

import com.kramp.aggregation.client.DownstreamClient;
import com.kramp.aggregation.dto.AggregatedProduct;
import com.kramp.aggregation.dto.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AggregationService {
    private final DownstreamClient client;

    public AggregationService(DownstreamClient client) {
        this.client = client;
    }

    public AggregatedProduct getProduct(String productId, String marketCode, String customerId) {
        validateMarket(marketCode);
        var productResult = client.getProduct(productId, marketCode);
        var priceResult = client.getPrice(productId, marketCode, customerId);
        var availabilityResult = client.getAvailability(productId, marketCode);
        Customer customer = null;
        String customerWarning = null;

        if (customerId != null && !customerId.isBlank()) {
            var customerResult = client.getCustomer(customerId, marketCode);
            customer = customerResult.data();
            customerWarning = customerResult.warning();
        }

        List<String> warnings = new ArrayList<>();
        addWarning(warnings, productResult.warning());
        addWarning(warnings, priceResult.warning());
        addWarning(warnings, availabilityResult.warning());
        addWarning(warnings, customerWarning);

        return new AggregatedProduct(
                productId,
                marketCode,
                productResult.data(),
                priceResult.data(),
                availabilityResult.data(),
                customer,
                warnings);
    }

    private void addWarning(List<String> warnings, String warning) {
        if (warning != null) warnings.add(warning);
    }

    private void validateMarket(String marketCode) {
        if (!List.of("en-EN", "nl-NL", "de-DE").contains(marketCode))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported market code: " + marketCode);
    }
}

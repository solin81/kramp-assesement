package com.kramp.aggregation.service;

import com.kramp.aggregation.client.AvailabilityClient;
import com.kramp.aggregation.client.CatalogClient;
import com.kramp.aggregation.client.CustomerClient;
import com.kramp.aggregation.client.PricingClient;
import com.kramp.aggregation.dto.AggregatedProduct;
import com.kramp.aggregation.dto.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AggregationService {
    private final CatalogClient catalogClient;
    private final PricingClient pricingClient;
    private final AvailabilityClient availabilityClient;
    private final CustomerClient customerClient;

    public AggregationService(CatalogClient catalogClient,
                              PricingClient pricingClient,
                              AvailabilityClient availabilityClient,
                              CustomerClient customerClient) {
        this.catalogClient = catalogClient;
        this.pricingClient = pricingClient;
        this.availabilityClient = availabilityClient;
        this.customerClient = customerClient;
    }

    public AggregatedProduct getProduct(String productId, String marketCode, String customerId) {
        validateMarket(marketCode);
        var productResult = catalogClient.getProduct(productId, marketCode);
        var priceResult = pricingClient.getPrice(productId, marketCode, customerId);
        var availabilityResult = availabilityClient.getAvailability(productId, marketCode);
        Customer customer = null;
        String customerWarning = null;

        if (customerId != null && !customerId.isBlank()) {
            var customerResult = customerClient.getCustomer(customerId, marketCode);
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

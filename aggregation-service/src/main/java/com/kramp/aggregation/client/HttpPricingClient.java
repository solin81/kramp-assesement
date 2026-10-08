package com.kramp.aggregation.client;

import com.kramp.aggregation.dto.Price;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(prefix = "upstream.pricing", name = "client", havingValue = "http", matchIfMissing = true)
public class HttpPricingClient extends HttpClientSupport implements PricingClient {
    private final String pricingUrl;

    public HttpPricingClient(RestClient http, @Value("${upstream.pricing-url}") String pricingUrl) {
        super(http);
        this.pricingUrl = pricingUrl;
    }

    @Override
    public ServiceResult<Price> getPrice(String productId, String marketCode, String customerId) {
        String customerParameter = customerId == null ? "" : "&customerId=" + customerId;
        return get(pricingUrl + "/api/prices/" + productId + "?marketCode=" + marketCode + customerParameter, Price.class, "pricing-service");
    }
}

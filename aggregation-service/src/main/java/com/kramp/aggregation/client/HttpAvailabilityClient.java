package com.kramp.aggregation.client;

import com.kramp.aggregation.dto.Availability;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(prefix = "upstream.availability", name = "client", havingValue = "http", matchIfMissing = true)
public class HttpAvailabilityClient extends HttpClientSupport implements AvailabilityClient {
    private final String availabilityUrl;

    public HttpAvailabilityClient(RestClient http, @Value("${upstream.availability-url}") String availabilityUrl) {
        super(http);
        this.availabilityUrl = availabilityUrl;
    }

    @Override
    public ServiceResult<Availability> getAvailability(String productId, String marketCode) {
        return get(availabilityUrl + "/api/availability/" + productId + "?marketCode=" + marketCode, Availability.class, "availability-service");
    }
}

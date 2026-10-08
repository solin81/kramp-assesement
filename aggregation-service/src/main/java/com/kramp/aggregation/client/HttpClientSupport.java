package com.kramp.aggregation.client;

import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

abstract class HttpClientSupport {
    private final RestClient http;

    HttpClientSupport(RestClient http) {
        this.http = http;
    }

    protected <T> ServiceResult<T> get(String url, Class<T> type, String serviceName) {
        try {
            T body = http.get().uri(url).retrieve().body(type);
            return body == null
                    ? ServiceResult.unavailable(serviceName + " returned an empty response")
                    : ServiceResult.available(body);
        } catch (RestClientResponseException exception) {
            String status = exception.getStatusCode().value() == 404 ? "has no matching data" : "is unavailable";
            return ServiceResult.unavailable(serviceName + " " + status + " (HTTP " + exception.getStatusCode()
                    .value() + ")");
        } catch (RestClientException exception) {
            return ServiceResult.unavailable(serviceName + " is unavailable");
        }
    }
}

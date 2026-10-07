package com.kramp.aggregation.client;

import com.kramp.aggregation.dto.Availability;
import com.kramp.aggregation.dto.Customer;
import com.kramp.aggregation.dto.Price;
import com.kramp.aggregation.dto.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class DownstreamClient {
    private final RestClient http;
    private final String catalog, pricing, availability, customer;

    public DownstreamClient(RestClient http,
                            @Value("${downstream.catalog-url}") String catalog,
                            @Value("${downstream.pricing-url}") String pricing,
                            @Value("${downstream.availability-url}") String availability,
                            @Value("${downstream.customer-url}") String customer) {
        this.http = http;
        this.catalog = catalog;
        this.pricing = pricing;
        this.availability = availability;
        this.customer = customer;
    }

    public ServiceResult<Product> getProduct(String id, String marketCode) {
        return get(catalog + "/api/products/" + id + "?marketCode=" + marketCode, Product.class, "catalog-service");
    }

    public ServiceResult<Price> getPrice(String id, String marketCode, String customerId) {
        String customerParameter = customerId == null ? "" : "&customerId=" + customerId;
        return get(pricing + "/api/prices/" + id + "?marketCode=" + marketCode + customerParameter, Price.class, "pricing-service");
    }

    public ServiceResult<Availability> getAvailability(String id, String marketCode) {
        return get(availability + "/api/availability/" + id + "?marketCode=" + marketCode, Availability.class, "availability-service");
    }

    public ServiceResult<Customer> getCustomer(String id, String marketCode) {
        return get(customer + "/api/customers/" + id + "?marketCode=" + marketCode, Customer.class, "customer-service");
    }

    private <T> ServiceResult<T> get(String url, Class<T> type, String name) {
        try {
            T body = http.get().uri(url).retrieve().body(type);
            return body == null ? ServiceResult.unavailable(name + " returned an empty response") : ServiceResult.available(body);
        } catch (RestClientResponseException exception) {
            String status = exception.getStatusCode().value() == 404 ? "has no matching data" : "is unavailable";
            return ServiceResult.unavailable(name + " " + status + " (HTTP " + exception.getStatusCode().value() + ")");
        } catch (RestClientException e) {
            return ServiceResult.unavailable(name + " is unavailable");
        }
    }
}

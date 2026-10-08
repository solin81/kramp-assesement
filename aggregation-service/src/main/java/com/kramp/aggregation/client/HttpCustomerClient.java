package com.kramp.aggregation.client;

import com.kramp.aggregation.dto.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(prefix = "upstream.customer", name = "client", havingValue = "http", matchIfMissing = true)
public class HttpCustomerClient extends HttpClientSupport implements CustomerClient {
    private final String customerUrl;

    public HttpCustomerClient(RestClient http, @Value("${upstream.customer-url}") String customerUrl) {
        super(http);
        this.customerUrl = customerUrl;
    }

    @Override
    public ServiceResult<Customer> getCustomer(String customerId, String marketCode) {
        return get(customerUrl + "/api/customers/" + customerId + "?marketCode=" + marketCode, Customer.class, "customer-service");
    }
}

package com.kramp.aggregation.client;

import com.kramp.aggregation.dto.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(prefix = "upstream.catalog", name = "client", havingValue = "http", matchIfMissing = true)
public class HttpCatalogClient extends HttpClientSupport implements CatalogClient {
    private final String catalogUrl;

    public HttpCatalogClient(RestClient http, @Value("${upstream.catalog-url}") String catalogUrl) {
        super(http);
        this.catalogUrl = catalogUrl;
    }

    @Override
    public ServiceResult<Product> getProduct(String productId, String marketCode) {
        return get(catalogUrl + "/api/products/" + productId + "?marketCode=" + marketCode, Product.class, "catalog-service");
    }
}

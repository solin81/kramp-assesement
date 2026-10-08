package com.kramp.aggregation;

import com.kramp.aggregation.client.*;
import com.kramp.aggregation.controller.AggregationController;
import com.kramp.aggregation.dto.Availability;
import com.kramp.aggregation.dto.Customer;
import com.kramp.aggregation.dto.Price;
import com.kramp.aggregation.dto.Product;
import com.kramp.aggregation.error.ApiErrorHandling;
import com.kramp.aggregation.service.AggregationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AggregationControllerTest {
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AggregationController(new AggregationService(
                        new StubCatalogClient(), new StubPricingClient(), new StubAvailabilityClient(), new StubCustomerClient())))
                .setControllerAdvice(new ApiErrorHandling.ApiExceptionHandler())
                .alwaysDo(print())
                .build();
    }

    @Test
    void getProduct_shouldReturnAggregatedProduct_whenMarketAndCustomerAreMissing() throws Exception {
        mvc.perform(get("/api/aggregated/products/P-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value("P-100"))
                .andExpect(jsonPath("$.marketCode").value("en-EN"))
                .andExpect(jsonPath("$.product.name").value("Product P-100"))
                .andExpect(jsonPath("$.customer").doesNotExist())
                .andExpect(jsonPath("$.warnings").isEmpty());
    }

    @Test
    void getProduct_shouldReturnLocalizedCustomerProduct_whenMarketIsNlNlAndCustomerExists() throws Exception {
        mvc.perform(get("/api/aggregated/products/P-200").param("marketCode", "nl-NL").param("customerId", "C-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marketCode").value("nl-NL"))
                .andExpect(jsonPath("$.product.name").value("Product P-200 (nl-NL)"))
                .andExpect(jsonPath("$.customer.id").value("C-100"))
                .andExpect(jsonPath("$.price.finalPrice").value(26.96));
    }

    @Test
    void getProduct_shouldReturnLocalizedProduct_whenMarketIsDeDeAndCustomerExists() throws Exception {
        mvc.perform(get("/api/aggregated/products/P-200").param("marketCode", "de-DE").param("customerId", "C-200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.name").value("Product P-200 (de-DE)"));
    }

    @Test
    void getProduct_shouldFail_whenCatalogCannotProvideProduct() throws Exception {
        mvc.perform(get("/api/aggregated/products/P-999").param("customerId", "C-999"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Catalog service is unavailable; product information cannot be returned"));
    }

    @Test
    void getProduct_shouldReturnProductWithWarnings_whenPricingAvailabilityAndCustomerFail() throws Exception {
        AggregationService service = new AggregationService(
                new StubCatalogClient(),
                (id, marketCode, customerId) -> ServiceResult.unavailable("pricing-service is unavailable"),
                (id, marketCode) -> ServiceResult.unavailable("availability-service is unavailable"),
                (id, marketCode) -> ServiceResult.unavailable("customer-service is unavailable"));

        MockMvc failureMvc = MockMvcBuilders.standaloneSetup(new AggregationController(service))
                .setControllerAdvice(new ApiErrorHandling.ApiExceptionHandler())
                .build();

        failureMvc.perform(get("/api/aggregated/products/P-100").param("customerId", "C-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.name").value("Product P-100"))
                .andExpect(jsonPath("$.price").doesNotExist())
                .andExpect(jsonPath("$.availability").doesNotExist())
                .andExpect(jsonPath("$.customer").doesNotExist())
                .andExpect(jsonPath("$.warnings.length()").value(3))
                .andExpect(jsonPath("$.warnings[0]").value("Price is unavailable"))
                .andExpect(jsonPath("$.warnings[1]").value("Stock is unknown"))
                .andExpect(jsonPath("$.warnings[2]").value("customer-service is unavailable"));
    }

    @Test
    void getProduct_shouldReturnBadRequest_whenMarketIsUnsupported() throws Exception {
        mvc.perform(get("/api/aggregated/products/P-100").param("marketCode", "fr-FR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported market code: fr-FR"));
    }

    private static final class StubCatalogClient implements CatalogClient {
        @Override
        public ServiceResult<Product> getProduct(String id, String marketCode) {
            return knownProduct(id)
                    ? ServiceResult.available(new Product(id, localized("Product " + id, marketCode), "Description", Map.of(), List.of()))
                    : ServiceResult.unavailable("catalog-service has no matching data (HTTP 404)");
        }
    }

    private static final class StubPricingClient implements PricingClient {
        @Override
        public ServiceResult<Price> getPrice(String id, String marketCode, String customerId) {
            if (!knownProduct(id)) return ServiceResult.unavailable("pricing-service has no matching data (HTTP 404)");
            BigDecimal finalPrice = "C-100".equals(customerId) ? new BigDecimal("26.96") : new BigDecimal("29.95");
            return ServiceResult.available(new Price(id, new BigDecimal("29.95"), new BigDecimal("2.99"), finalPrice));
        }
    }

    private static final class StubAvailabilityClient implements AvailabilityClient {
        @Override
        public ServiceResult<Availability> getAvailability(String id, String marketCode) {
            return knownProduct(id)
                    ? ServiceResult.available(new Availability(id, 42, "Warehouse", "Tomorrow"))
                    : ServiceResult.unavailable("availability-service has no matching data (HTTP 404)");
        }
    }

    private static final class StubCustomerClient implements CustomerClient {
        @Override
        public ServiceResult<Customer> getCustomer(String id, String marketCode) {
            return ("C-100".equals(id) || "C-200".equals(id))
                    ? ServiceResult.available(new Customer(id, "PROFESSIONAL", List.of("Fast delivery")))
                    : ServiceResult.unavailable("customer-service has no matching data (HTTP 404)");
        }
    }

    private static boolean knownProduct(String id) {
        return "P-100".equals(id) || "P-200".equals(id);
    }

    private static String localized(String value, String marketCode) {
        return "en-EN".equals(marketCode) ? value : value + " (" + marketCode + ")";
    }
}

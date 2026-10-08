package com.kramp.pricing;

import com.kramp.pricing.controller.PricingController;
import com.kramp.pricing.error.ApiErrorHandling;
import com.kramp.pricing.service.InMemoryPricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PricingControllerTest {
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new PricingController(new InMemoryPricingService()))
                .setControllerAdvice(new ApiErrorHandling.ApiExceptionHandler())
                .alwaysDo(print())
                .build();
    }

    @Test
    void getPrices_shouldReturnBasePrices_whenCustomerIdIsMissing() throws Exception {
        mvc.perform(get("/api/prices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.productId == 'P-100')].finalPrice").value(29.95));
    }

    @Test
    void getPrices_shouldApplyTenPercentDiscount_whenCustomerIsC100() throws Exception {
        mvc.perform(get("/api/prices").param("marketCode", "nl-NL")
                        .param("customerId", "C-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.productId == 'P-100')].customerDiscount").value(2.99))
                .andExpect(jsonPath("$[?(@.productId == 'P-100')].finalPrice").value(26.96));
    }

    @Test
    void getPrices_shouldApplyFifteenPercentDiscount_whenCustomerIsC200() throws Exception {
        mvc.perform(get("/api/prices").param("marketCode", "de-DE")
                        .param("customerId", "C-200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.productId == 'P-200')].finalPrice").value(10.63));
    }

    @Test
    void getPrice_shouldReturnBasePrice_whenCustomerIdIsMissing() throws Exception {
        mvc.perform(get("/api/prices/P-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basePrice").value(29.95))
                .andExpect(jsonPath("$.customerDiscount").value(0));
    }

    @Test
    void getPrice_shouldApplyCustomerDiscount_whenCustomerIsC100() throws Exception {
        mvc.perform(get("/api/prices/P-100").param("marketCode", "en-EN")
                        .param("customerId", "C-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerDiscount").value(2.99))
                .andExpect(jsonPath("$.finalPrice").value(26.96));
    }

    @Test
    void getPrice_shouldApplyCustomerDiscount_whenCustomerIsC200() throws Exception {
        mvc.perform(get("/api/prices/P-200").param("marketCode", "de-DE")
                        .param("customerId", "C-200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finalPrice").value(10.63));
    }

    @Test
    void getPrice_shouldReturnBasePrice_whenCustomerIsUnknown() throws Exception {
        mvc.perform(get("/api/prices/P-100").param("customerId", "C-999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerDiscount").value(0))
                .andExpect(jsonPath("$.finalPrice").value(29.95));
    }

    @Test
    void getPrices_shouldReturnBadRequest_whenMarketIsUnsupported() throws Exception {
        mvc.perform(get("/api/prices").param("marketCode", "fr-FR"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPrice_shouldReturnBadRequest_whenMarketIsUnsupported() throws Exception {
        mvc.perform(get("/api/prices/P-100").param("marketCode", "fr-FR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported market code: fr-FR"));
    }

    @Test
    void getPrice_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        mvc.perform(get("/api/prices/P-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Price not found: P-999"));
    }
}

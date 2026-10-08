package com.kramp.availability;

import com.kramp.availability.controller.AvailabilityController;
import com.kramp.availability.error.ApiErrorHandling;
import com.kramp.availability.service.InMemoryAvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AvailabilityControllerTest {
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AvailabilityController(new InMemoryAvailabilityService()))
                .setControllerAdvice(new ApiErrorHandling.ApiExceptionHandler())
                .alwaysDo(print())
                .build();
    }

    @Test
    void getAvailability_shouldReturnEnglishAvailability_whenMarketIsMissing() throws Exception {
        mvc.perform(get("/api/availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.productId == 'P-100')].warehouseLocation").value("Veghel Distribution Center"));
    }

    @Test
    void getAvailability_shouldReturnDutchAvailability_whenMarketIsNlNl() throws Exception {
        mvc.perform(get("/api/availability").param("marketCode", "nl-NL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.productId == 'P-100')].warehouseLocation").value("Distributiecentrum Veghel"));
    }

    @Test
    void getAvailability_shouldReturnGermanAvailability_whenMarketIsDeDe() throws Exception {
        mvc.perform(get("/api/availability").param("marketCode", "de-DE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.productId == 'P-100')].warehouseLocation").value("Hamm Distribution Center"));
    }

    @Test
    void getAvailabilityByProductId_shouldReturnStockAndDelivery_whenProductIsInStock() throws Exception {
        mvc.perform(get("/api/availability/P-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockLevel").value(42))
                .andExpect(jsonPath("$.expectedDelivery").value("Next business day"));
    }

    @Test
    void getAvailabilityByProductId_shouldReturnDelayedDelivery_whenProductIsOutOfStock() throws Exception {
        mvc.perform(get("/api/availability/P-200").param("marketCode", "de-DE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockLevel").value(0))
                .andExpect(jsonPath("$.expectedDelivery").value("5–7 Werktage"));
    }

    @Test
    void getAvailability_shouldReturnBadRequest_whenMarketIsUnsupported() throws Exception {
        mvc.perform(get("/api/availability").param("marketCode", "fr-FR")).andExpect(status().isBadRequest());
    }

    @Test
    void getAvailabilityByProductId_shouldReturnBadRequest_whenMarketIsUnsupported() throws Exception {
        mvc.perform(get("/api/availability/P-100").param("marketCode", "fr-FR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported market code: fr-FR"));
    }

    @Test
    void getAvailabilityByProductId_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        mvc.perform(get("/api/availability/P-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Availability not found: P-999"));
    }
}

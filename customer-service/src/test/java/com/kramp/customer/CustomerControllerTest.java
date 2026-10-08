package com.kramp.customer;

import com.kramp.customer.controller.CustomerController;
import com.kramp.customer.error.ApiErrorHandling;
import com.kramp.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerControllerTest {
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new CustomerController(new CustomerService()))
                .setControllerAdvice(new ApiErrorHandling.ApiExceptionHandler())
                .alwaysDo(print())
                .build();
    }

    @Test
    void getCustomers_shouldReturnEnglishPreferences_whenMarketIsMissing() throws Exception {
        mvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.id == 'C-100')].preferences[0]").value("Prefer fast delivery"));
    }

    @Test
    void getCustomers_shouldReturnDutchPreferences_whenMarketIsNlNl() throws Exception {
        mvc.perform(get("/api/customers").param("marketCode", "nl-NL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 'C-100')].preferences[0]").value("Snelle levering gewenst"));
    }

    @Test
    void getCustomers_shouldReturnGermanPreferences_whenMarketIsDeDe() throws Exception {
        mvc.perform(get("/api/customers").param("marketCode", "de-DE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 'C-100')].preferences[0]").value("Schnelle Lieferung bevorzugt"));
    }

    @Test
    void getCustomer_shouldReturnSegment_whenMarketIsMissing() throws Exception {
        mvc.perform(get("/api/customers/C-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.segment").value("PROFESSIONAL"));
    }

    @Test
    void getCustomer_shouldReturnDutchPreferences_whenMarketIsNlNl() throws Exception {
        mvc.perform(get("/api/customers/C-200").param("marketCode", "nl-NL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferences[0]").value("Verzamelfactuur"));
    }

    @Test
    void getCustomer_shouldReturnGermanPreferences_whenMarketIsDeDe() throws Exception {
        mvc.perform(get("/api/customers/C-200").param("marketCode", "de-DE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferences[0]").value("Sammelrechnung"));
    }

    @Test
    void getCustomers_shouldReturnBadRequest_whenMarketIsUnsupported() throws Exception {
        mvc.perform(get("/api/customers").param("marketCode", "fr-FR")).andExpect(status().isBadRequest());
    }

    @Test
    void getCustomer_shouldReturnBadRequest_whenMarketIsUnsupported() throws Exception {
        mvc.perform(get("/api/customers/C-100").param("marketCode", "fr-FR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported market code: fr-FR"));
    }

    @Test
    void getCustomer_shouldReturnNotFound_whenCustomerDoesNotExist() throws Exception {
        mvc.perform(get("/api/customers/C-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found: C-999"));
    }
}

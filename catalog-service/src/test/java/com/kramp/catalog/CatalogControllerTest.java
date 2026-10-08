package com.kramp.catalog;

import com.kramp.catalog.controller.CatalogController;
import com.kramp.catalog.error.ApiErrorHandling;
import com.kramp.catalog.service.InMemoryCatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CatalogControllerTest {
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new CatalogController(new InMemoryCatalogService()))
                .setControllerAdvice(new ApiErrorHandling.ApiExceptionHandler())
                .alwaysDo(print())
                .build();
    }

    @Test
    void getProducts_shouldReturnEnglishProducts_whenMarketIsMissing() throws Exception {
        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.id == 'P-100')].name").value("Hydraulic Filter"));
    }

    @Test
    void getProducts_shouldReturnDutchProducts_whenMarketIsNlNl() throws Exception {
        mvc.perform(get("/api/products").param("marketCode", "nl-NL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 'P-100')].name").value("Hydraulisch filter"));
    }

    @Test
    void getProducts_shouldReturnGermanProducts_whenMarketIsDeDe() throws Exception {
        mvc.perform(get("/api/products").param("marketCode", "de-DE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 'P-100')].name").value("Hydraulikfilter"));
    }

    @Test
    void getProduct_shouldReturnEnglishProduct_whenMarketIsMissing() throws Exception {
        mvc.perform(get("/api/products/P-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("P-100"))
                .andExpect(jsonPath("$.name").value("Hydraulic Filter"));
    }

    @Test
    void getProduct_shouldReturnDutchProduct_whenMarketIsNlNl() throws Exception {
        mvc.perform(get("/api/products/P-200").param("marketCode", "nl-NL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Werkhandschoenen"));
    }

    @Test
    void getProduct_shouldReturnGermanProduct_whenMarketIsDeDe() throws Exception {
        mvc.perform(get("/api/products/P-200").param("marketCode", "de-DE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Arbeitshandschuhe"));
    }

    @Test
    void getProducts_shouldReturnBadRequest_whenMarketIsUnsupported() throws Exception {
        mvc.perform(get("/api/products").param("marketCode", "fr-FR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported market code: fr-FR"));
    }

    @Test
    void getProduct_shouldReturnBadRequest_whenMarketIsUnsupported() throws Exception {
        mvc.perform(get("/api/products/P-100").param("marketCode", "fr-FR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value("/api/products/P-100"));
    }

    @Test
    void getProduct_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        mvc.perform(get("/api/products/P-999").param("marketCode", "en-EN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found: P-999"));
    }
}

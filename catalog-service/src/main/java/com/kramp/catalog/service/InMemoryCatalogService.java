package com.kramp.catalog.service;

import com.kramp.catalog.dto.Product;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(prefix = "catalog", name = "source", havingValue = "in-memory", matchIfMissing = true)
public class InMemoryCatalogService implements CatalogService {
    private final ServiceBehaviour behaviour;
    private final Map<String, Product> products = Map.of(
            "P-100", new Product("P-100", "Hydraulic Filter", "High-flow filter for agricultural hydraulic systems.", Map.of("Thread", "1 inch BSP", "Micron rating", "10 μm", "Material", "Steel"), List.of("https://images.example.test/products/P-100-main.jpg", "https://images.example.test/products/P-100-detail.jpg")),
            "P-200", new Product("P-200", "Work Gloves", "Durable nitrile-coated gloves for everyday workshop work.", Map.of("Size range", "M–XL", "Coating", "Nitrile", "Standard", "EN 388"), List.of("https://images.example.test/products/P-200-main.jpg")));

    @Autowired
    public InMemoryCatalogService(ServiceBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    public InMemoryCatalogService() {
        this.behaviour = null;
    }

    public Collection<Product> getAllProducts(String marketCode) {
        simulate();
        validateMarket(marketCode);
        return products.values().stream().map(product -> localize(product, marketCode)).toList();
    }

    public Product byProductId(String productId, String marketCode) {
        simulate();
        validateMarket(marketCode);
        var p = products.get(productId);
        if (p == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + productId);
        return localize(p, marketCode);
    }

    private void simulate() {
        if (behaviour != null) behaviour.simulate();
    }

    private void validateMarket(String marketCode) {
        if (!List.of("en-EN", "nl-NL", "de-DE").contains(marketCode))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported market code: " + marketCode);
    }

    private Product localize(Product product, String marketCode) {
        return switch (marketCode) {
            case "en-EN" -> product.id().equals("P-100") ?
                            new Product(product.id(), "Hydraulic Filter", "High-flow filter for agricultural hydraulic systems.", Map.of("Thread", "1 inch BSP", "Micron rating", "10 μm", "Material", "Steel"), product.imageUrls()) :
                            new Product(product.id(), "Work Gloves", "Durable nitrile-coated gloves for everyday workshop work.", Map.of("Size range", "M–XL", "Coating", "Nitrile", "Standard", "EN 388"), product.imageUrls());
            case "nl-NL" ->
                    product.id().equals("P-100") ?
                            new Product(product.id(), "Hydraulisch filter", "Hoogdoorstroomfilter voor hydraulische systemen in de landbouw.", Map.of("Schroefdraad", "1 inch BSP", "Filtratiegraad", "10 μm", "Materiaal", "Staal"), product.imageUrls()) :
                            new Product(product.id(), "Werkhandschoenen", "Duurzame nitrilgecoate handschoenen voor dagelijks werk in de werkplaats.", Map.of("Maatbereik", "M–XL", "Coating", "Nitril", "Norm", "EN 388"), product.imageUrls());
            case "de-DE" ->
                    product.id().equals("P-100") ?
                            new Product(product.id(), "Hydraulikfilter", "Hochleistungsfilter für landwirtschaftliche Hydrauliksysteme.", Map.of("Gewinde", "1 Zoll BSP", "Filterfeinheit", "10 μm", "Material", "Stahl"), product.imageUrls()) :
                            new Product(product.id(), "Arbeitshandschuhe", "Robuste, nitrilbeschichtete Handschuhe für tägliche Werkstattarbeiten.", Map.of("Größen", "M–XL", "Beschichtung", "Nitril", "Norm", "EN 388"), product.imageUrls());
            default -> throw new IllegalStateException("Validated market code expected");
        };
    }
}

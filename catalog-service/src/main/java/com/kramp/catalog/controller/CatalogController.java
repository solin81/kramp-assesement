package com.kramp.catalog.controller;

import com.kramp.catalog.dto.Product;
import com.kramp.catalog.service.CatalogService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/products")
public class CatalogController {
    private final CatalogService service;

    public CatalogController(CatalogService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Product> getAllProducts(@RequestParam(name = "marketCode", defaultValue = "en-EN") String marketCode) {
        return service.getAllProducts(marketCode);
    }

    @GetMapping("/{productId}")
    public Product byProductId(@PathVariable("productId") String productId,
                               @RequestParam(name = "marketCode", defaultValue = "en-EN") String marketCode) {
        return service.byProductId(productId, marketCode);
    }
}

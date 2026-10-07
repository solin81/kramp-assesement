package com.kramp.availability.controller;

import com.kramp.availability.dto.Availability;
import com.kramp.availability.service.AvailabilityService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {
    private final AvailabilityService service;

    public AvailabilityController(AvailabilityService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Availability> getAllAvailability(@RequestParam(name = "marketCode", defaultValue = "en-EN") String marketCode) {
        return service.getAllAvailability(marketCode);
    }

    @GetMapping("/{productId}")
    public Availability byProductId(@PathVariable("productId") String productId,
                                    @RequestParam(name = "marketCode", defaultValue = "en-EN") String marketCode) {
        return service.byProductId(productId, marketCode);
    }
}

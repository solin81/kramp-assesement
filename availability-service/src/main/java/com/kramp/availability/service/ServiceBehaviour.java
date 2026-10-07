package com.kramp.availability.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulates inventory-system latency and its configured reliability percentage.
 */
@Component
public class ServiceBehaviour {
    private final long latencyMs;
    private final double reliabilityPercent;

    public ServiceBehaviour(@Value("${service-behavior.latency-ms}") long latencyMs,
                            @Value("${service-behavior.reliability-percent}") double reliabilityPercent) {
        this.latencyMs = latencyMs;
        this.reliabilityPercent = reliabilityPercent;
    }

    public void simulate() {
        try {
            Thread.sleep(latencyMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Availability service request interrupted", exception);
        }
        if (ThreadLocalRandom.current().nextDouble(100) >= reliabilityPercent)
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Availability service temporarily unavailable (simulated)");
    }
}

package com.kramp.availability.service;

import com.kramp.availability.dto.Availability;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(prefix = "availability", name = "source", havingValue = "in-memory", matchIfMissing = true)
public class InMemoryAvailabilityService implements AvailabilityService {
    private final ServiceBehaviour behaviour;
    private final Map<String, Integer> stock = Map.of("P-100", 42, "P-200", 0);

    @Autowired
    public InMemoryAvailabilityService(ServiceBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    public InMemoryAvailabilityService() {
        this.behaviour = null;
    }

    public Availability byProductId(String productId, String marketCode) {
        simulate();
        validateMarket(marketCode);
        if (!stock.containsKey(productId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Availability not found: " + productId);
        return localizedAvailability(productId, marketCode);
    }

    public Collection<Availability> getAllAvailability(String marketCode) {
        simulate();
        validateMarket(marketCode);
        return stock.keySet().stream().map(id -> localizedAvailability(id, marketCode)).toList();
    }

    private Availability localizedAvailability(String productId, String marketCode) {
        int level = stock.get(productId);
        return switch (marketCode) {
            case "en-EN" ->
                    new Availability(productId, level, "Veghel Distribution Center", level > 0 ? "Next business day" : "5–7 business days");
            case "nl-NL" ->
                    new Availability(productId, level, "Distributiecentrum Veghel", level > 0 ? "Volgende werkdag" : "5–7 werkdagen");
            case "de-DE" ->
                    new Availability(productId, level, "Hamm Distribution Center", level > 0 ? "Nächster Werktag" : "5–7 Werktage");
            default -> throw new IllegalStateException("Validated market code expected");
        };
    }

    private void simulate() {
        if (behaviour != null) behaviour.simulate();
    }

    private void validateMarket(String marketCode) {
        if (!List.of("en-EN", "nl-NL", "de-DE").contains(marketCode))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported market code: " + marketCode);
    }
}

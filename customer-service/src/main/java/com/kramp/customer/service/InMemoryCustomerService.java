package com.kramp.customer.service;

import com.kramp.customer.dto.Customer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(prefix = "customer", name = "source", havingValue = "in-memory", matchIfMissing = true)
public class InMemoryCustomerService implements CustomerService {
    private final ServiceBehaviour behaviour;
    private final Map<String, Customer> customers = Map.of(
            "C-100", new Customer("C-100", "PROFESSIONAL", List.of("Prefer fast delivery", "Email order confirmations")),
            "C-200", new Customer("C-200", "BUSINESS", List.of("Consolidated invoicing", "Pallet delivery")));

    @Autowired
    public InMemoryCustomerService(ServiceBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    public InMemoryCustomerService() {
        this.behaviour = null;
    }

    public Collection<Customer> getAllCustomers(String marketCode) {
        simulate();
        validateMarket(marketCode);
        return customers.values().stream().map(customer -> localize(customer, marketCode)).toList();
    }

    public Customer byCustomerId(String customerId, String marketCode) {
        simulate();
        validateMarket(marketCode);
        var c = customers.get(customerId);
        if (c == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + customerId);
        return localize(c, marketCode);
    }

    private void simulate() {
        if (behaviour != null) behaviour.simulate();
    }

    private void validateMarket(String marketCode) {
        if (!List.of("en-EN", "nl-NL", "de-DE").contains(marketCode))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported market code: " + marketCode);
    }

    private Customer localize(Customer customer, String marketCode) {
        return switch (marketCode) {
            case "en-EN" -> customer.id().equals("C-100") ?
                    new Customer(customer.id(), customer.segment(), List.of("Prefer fast delivery", "Email order confirmations")) :
                    new Customer(customer.id(), customer.segment(), List.of("Consolidated invoicing", "Pallet delivery"));
            case "nl-NL" -> customer.id().equals("C-100") ?
                    new Customer(customer.id(), customer.segment(), List.of("Snelle levering gewenst", "Orderbevestiging per e-mail")) :
                    new Customer(customer.id(), customer.segment(), List.of("Verzamelfactuur", "Levering op pallet"));
            case "de-DE" -> customer.id().equals("C-100") ?
                    new Customer(customer.id(), customer.segment(), List.of("Schnelle Lieferung bevorzugt", "Auftragsbestätigung per E-Mail")) :
                    new Customer(customer.id(), customer.segment(), List.of("Sammelrechnung", "Palettenlieferung"));
            default -> throw new IllegalStateException("Validated market code expected");
        };
    }
}

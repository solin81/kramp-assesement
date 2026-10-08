package com.kramp.availability.service;

import com.kramp.availability.dto.Availability;

import java.util.Collection;

/** Contract for retrieving availability from the configured inventory source. */
public interface AvailabilityService {
    Availability byProductId(String productId, String marketCode);

    Collection<Availability> getAllAvailability(String marketCode);
}

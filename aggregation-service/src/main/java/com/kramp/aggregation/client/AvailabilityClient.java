package com.kramp.aggregation.client;

import com.kramp.aggregation.dto.Availability;

/**
 * Port used by aggregation to retrieve availability data.
 */
public interface AvailabilityClient {
    ServiceResult<Availability> getAvailability(String productId, String marketCode);
}

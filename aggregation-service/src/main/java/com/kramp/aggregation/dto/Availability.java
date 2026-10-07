package com.kramp.aggregation.dto;

public record Availability(String productId,
                           int stockLevel,
                           String warehouseLocation,
                           String expectedDelivery) {
}

package com.kramp.availability.dto;

public record Availability(String productId,
                           int stockLevel,
                           String warehouseLocation,
                           String expectedDelivery) {
}

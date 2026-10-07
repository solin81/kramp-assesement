package com.kramp.aggregation.dto;

import java.util.List;

public record AggregatedProduct(String productId,
                                String marketCode,
                                Product product,
                                Price price,
                                Availability availability,
                                Customer customer,
                                List<String> warnings) {
}

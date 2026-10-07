package com.kramp.aggregation.dto;

import java.math.BigDecimal;

public record Price(String productId,
                    BigDecimal basePrice,
                    BigDecimal customerDiscount,
                    BigDecimal finalPrice) {
}

package com.kramp.catalog.dto;

import java.util.List;
import java.util.Map;

public record Product(String id,
                      String name,
                      String description,
                      Map<String, String> specifications,
                      List<String> imageUrls) {
}

package com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.response;

import java.math.BigDecimal;

public record ArticleRestResponse(
    String id,
    String description,
    String provider,
    String brand,
    String model,
    String category,
    String store,
    Integer quantity,
    BigDecimal purchasePrice,
    BigDecimal salePrice,
    BigDecimal iva) {}

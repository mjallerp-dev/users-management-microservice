package com.jcaa.usersmanagement.infrastructure.adapter.persistence.dto;

import java.math.BigDecimal;

public record ArticlePersistenceDto(
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
    BigDecimal iva,
    String createdAt,
    String updatedAt) {}

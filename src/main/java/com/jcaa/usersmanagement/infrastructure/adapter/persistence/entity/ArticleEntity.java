package com.jcaa.usersmanagement.infrastructure.adapter.persistence.entity;

import java.math.BigDecimal;

public record ArticleEntity(
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

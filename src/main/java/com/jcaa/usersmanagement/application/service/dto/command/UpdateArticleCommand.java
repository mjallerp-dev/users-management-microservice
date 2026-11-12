package com.jcaa.usersmanagement.application.service.dto.command;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateArticleCommand(
    @NotBlank(message = "id must not be blank") String id,
    @NotBlank(message = "description must not be blank")
        @Size(min = 3, message = "description must have at least 3 characters")
        String description,
    @NotBlank(message = "provider must not be blank")
        @Size(min = 3, message = "provider must have at least 3 characters")
        String provider,
    @NotBlank(message = "brand must not be blank")
        @Size(min = 3, message = "brand must have at least 3 characters")
        String brand,
    @NotBlank(message = "model must not be blank")
        @Size(min = 3, message = "model must have at least 3 characters")
        String model,
    @NotBlank(message = "category must not be blank") String category,
    @NotBlank(message = "store must not be blank")
        @Size(min = 3, message = "store must have at least 3 characters")
        String store,
    @NotNull(message = "quantity must not be null")
        @PositiveOrZero(message = "quantity must be greater than or equal to zero")
        Integer quantity,
    @NotNull(message = "purchasePrice must not be null")
        @Positive(message = "purchasePrice must be greater than zero")
        BigDecimal purchasePrice,
    @NotNull(message = "salePrice must not be null")
        @Positive(message = "salePrice must be greater than zero")
        BigDecimal salePrice,
    @NotNull(message = "iva must not be null")
        @DecimalMin(value = "0", message = "iva must be greater than or equal to zero")
        @DecimalMax(value = "100", message = "iva must be less than or equal to one hundred")
        BigDecimal iva)
{

}

package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidArticleSalePriceException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record ArticleSalePrice(BigDecimal value) {

  private static final int SCALE = 2;

  public ArticleSalePrice {
    validateNotEmpty(value);
    final BigDecimal normalizedValue = value.setScale(SCALE, RoundingMode.HALF_UP);
    validateGreaterThanZero(normalizedValue);
    value = normalizedValue;
  }

  private static void validateNotEmpty(final BigDecimal value) {
    if (Objects.isNull(value)) {
      throw InvalidArticleSalePriceException.becauseValueIsEmpty();
    }
  }

  private static void validateGreaterThanZero(final BigDecimal normalizedValue) {
    if (normalizedValue.signum() <= 0) {
      throw InvalidArticleSalePriceException.becauseMustBeGreaterThanZero();
    }
  }

  @Override
  public String toString() {
    return value.toPlainString();
  }
}

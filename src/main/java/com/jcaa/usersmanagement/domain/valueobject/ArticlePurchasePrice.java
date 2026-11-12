package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidArticlePurchasePriceException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record ArticlePurchasePrice(BigDecimal value) {

  private static final int SCALE = 2;

  public ArticlePurchasePrice {
    validateNotEmpty(value);
    final BigDecimal normalizedValue = value.setScale(SCALE, RoundingMode.HALF_UP);
    validateGreaterThanZero(normalizedValue);
    value = normalizedValue;
  }

  private static void validateNotEmpty(final BigDecimal value) {
    if (Objects.isNull(value)) {
      throw InvalidArticlePurchasePriceException.becauseValueIsEmpty();
    }
  }

  private static void validateGreaterThanZero(final BigDecimal normalizedValue) {
    if (normalizedValue.signum() <= 0) {
      throw InvalidArticlePurchasePriceException.becauseMustBeGreaterThanZero();
    }
  }

  @Override
  public String toString() {
    return value.toPlainString();
  }
}

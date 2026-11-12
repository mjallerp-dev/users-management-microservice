package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidArticleIvaException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record ArticleIva(BigDecimal value) {

  private static final int SCALE = 2;
  private static final BigDecimal MAXIMUM_VALUE = BigDecimal.valueOf(100);

  public ArticleIva {
    validateNotEmpty(value);
    final BigDecimal normalizedValue = value.setScale(SCALE, RoundingMode.HALF_UP);
    validateNotNegative(normalizedValue);
    validateNotAboveMaximum(normalizedValue);
    value = normalizedValue;
  }

  private static void validateNotEmpty(final BigDecimal value) {
    if (Objects.isNull(value)) {
      throw InvalidArticleIvaException.becauseValueIsEmpty();
    }
  }

  private static void validateNotNegative(final BigDecimal normalizedValue) {
    if (normalizedValue.signum() < 0) {
      throw InvalidArticleIvaException.becauseMustBeGreaterThanOrEqualToZero();
    }
  }

  private static void validateNotAboveMaximum(final BigDecimal normalizedValue) {
    if (normalizedValue.compareTo(MAXIMUM_VALUE) > 0) {
      throw InvalidArticleIvaException.becauseMustBeLessThanOrEqualToOneHundred();
    }
  }

  @Override
  public String toString() {
    return value.toPlainString();
  }
}

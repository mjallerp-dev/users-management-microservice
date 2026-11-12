package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidArticleQuantityException;
import java.util.Objects;

public record ArticleQuantity(Integer value) {

  private static final int MINIMUM_VALUE = 0;

  public ArticleQuantity {
    validateNotEmpty(value);
    validateNotNegative(value);
  }

  private static void validateNotEmpty(final Integer value) {
    if (Objects.isNull(value)) {
      throw InvalidArticleQuantityException.becauseValueIsEmpty();
    }
  }

  private static void validateNotNegative(final Integer value) {
    if (value < MINIMUM_VALUE) {
      throw InvalidArticleQuantityException.becauseMustBeGreaterThanOrEqualToZero();
    }
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}

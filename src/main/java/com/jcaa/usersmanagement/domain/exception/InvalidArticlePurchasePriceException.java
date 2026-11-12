package com.jcaa.usersmanagement.domain.exception;

public final class InvalidArticlePurchasePriceException extends DomainException {

  private static final String MESSAGE_EMPTY = "The article purchase price must not be empty.";
  private static final String MESSAGE_NOT_POSITIVE =
      "The article purchase price must be greater than zero.";

  private InvalidArticlePurchasePriceException(final String message) {
    super(message);
  }

  public static InvalidArticlePurchasePriceException becauseValueIsEmpty() {
    return new InvalidArticlePurchasePriceException(MESSAGE_EMPTY);
  }

  public static InvalidArticlePurchasePriceException becauseMustBeGreaterThanZero() {
    return new InvalidArticlePurchasePriceException(MESSAGE_NOT_POSITIVE);
  }
}

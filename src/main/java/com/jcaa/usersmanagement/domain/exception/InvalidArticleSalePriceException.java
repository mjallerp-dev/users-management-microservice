package com.jcaa.usersmanagement.domain.exception;

public final class InvalidArticleSalePriceException extends DomainException {

  private static final String MESSAGE_EMPTY = "The article sale price must not be empty.";
  private static final String MESSAGE_NOT_POSITIVE =
      "The article sale price must be greater than zero.";

  private InvalidArticleSalePriceException(final String message) {
    super(message);
  }

  public static InvalidArticleSalePriceException becauseValueIsEmpty() {
    return new InvalidArticleSalePriceException(MESSAGE_EMPTY);
  }

  public static InvalidArticleSalePriceException becauseMustBeGreaterThanZero() {
    return new InvalidArticleSalePriceException(MESSAGE_NOT_POSITIVE);
  }
}

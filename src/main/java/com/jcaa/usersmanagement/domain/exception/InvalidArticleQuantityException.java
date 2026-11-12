package com.jcaa.usersmanagement.domain.exception;

public final class InvalidArticleQuantityException extends DomainException {

  private static final String MESSAGE_EMPTY = "The article quantity must not be empty.";
  private static final String MESSAGE_NEGATIVE =
      "The article quantity must be greater than or equal to zero.";

  private InvalidArticleQuantityException(final String message) {
    super(message);
  }

  public static InvalidArticleQuantityException becauseValueIsEmpty() {
    return new InvalidArticleQuantityException(MESSAGE_EMPTY);
  }

  public static InvalidArticleQuantityException becauseMustBeGreaterThanOrEqualToZero() {
    return new InvalidArticleQuantityException(MESSAGE_NEGATIVE);
  }
}

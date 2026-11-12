package com.jcaa.usersmanagement.domain.exception;

public final class InvalidArticleIvaException extends DomainException {

  private static final String MESSAGE_EMPTY = "The article IVA must not be empty.";
  private static final String MESSAGE_BELOW_MINIMUM =
      "The article IVA must be greater than or equal to zero.";
  private static final String MESSAGE_ABOVE_MAXIMUM =
      "The article IVA must be less than or equal to one hundred.";

  private InvalidArticleIvaException(final String message) {
    super(message);
  }

  public static InvalidArticleIvaException becauseValueIsEmpty() {
    return new InvalidArticleIvaException(MESSAGE_EMPTY);
  }

  public static InvalidArticleIvaException becauseMustBeGreaterThanOrEqualToZero() {
    return new InvalidArticleIvaException(MESSAGE_BELOW_MINIMUM);
  }

  public static InvalidArticleIvaException becauseMustBeLessThanOrEqualToOneHundred() {
    return new InvalidArticleIvaException(MESSAGE_ABOVE_MAXIMUM);
  }
}

package com.jcaa.usersmanagement.domain.exception;

public final class InvalidArticleBrandException extends DomainException {

  private static final String MESSAGE_EMPTY = "The article brand must not be empty.";
  private static final String MESSAGE_TOO_SHORT =
      "The article brand must have at least %d characters.";

  private InvalidArticleBrandException(final String message) {
    super(message);
  }

  public static InvalidArticleBrandException becauseValueIsEmpty() {
    return new InvalidArticleBrandException(MESSAGE_EMPTY);
  }

  public static InvalidArticleBrandException becauseLengthIsTooShort(final int minimumLength) {
    return new InvalidArticleBrandException(String.format(MESSAGE_TOO_SHORT, minimumLength));
  }
}

package com.jcaa.usersmanagement.domain.exception;

public final class InvalidArticleStoreException extends DomainException {

  private static final String MESSAGE_EMPTY = "The article store must not be empty.";
  private static final String MESSAGE_TOO_SHORT =
      "The article store must have at least %d characters.";

  private InvalidArticleStoreException(final String message) {
    super(message);
  }

  public static InvalidArticleStoreException becauseValueIsEmpty() {
    return new InvalidArticleStoreException(MESSAGE_EMPTY);
  }

  public static InvalidArticleStoreException becauseLengthIsTooShort(final int minimumLength) {
    return new InvalidArticleStoreException(String.format(MESSAGE_TOO_SHORT, minimumLength));
  }
}

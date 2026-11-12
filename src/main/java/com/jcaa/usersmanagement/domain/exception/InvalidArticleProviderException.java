package com.jcaa.usersmanagement.domain.exception;

public final class InvalidArticleProviderException extends DomainException {

  private static final String MESSAGE_EMPTY = "The article provider must not be empty.";
  private static final String MESSAGE_TOO_SHORT =
      "The article provider must have at least %d characters.";

  private InvalidArticleProviderException(final String message) {
    super(message);
  }

  public static InvalidArticleProviderException becauseValueIsEmpty() {
    return new InvalidArticleProviderException(MESSAGE_EMPTY);
  }

  public static InvalidArticleProviderException becauseLengthIsTooShort(final int minimumLength) {
    return new InvalidArticleProviderException(String.format(MESSAGE_TOO_SHORT, minimumLength));
  }
}

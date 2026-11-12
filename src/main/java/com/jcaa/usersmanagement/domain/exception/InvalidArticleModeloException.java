package com.jcaa.usersmanagement.domain.exception;

public final class InvalidArticleModeloException extends DomainException {

  private static final String MESSAGE_EMPTY = "The article model must not be empty.";
  private static final String MESSAGE_TOO_SHORT =
      "The article model must have at least %d characters.";

  private InvalidArticleModeloException(final String message) {
    super(message);
  }

  public static InvalidArticleModeloException becauseValueIsEmpty() {
    return new InvalidArticleModeloException(MESSAGE_EMPTY);
  }

  public static InvalidArticleModeloException becauseLengthIsTooShort(final int minimumLength) {
    return new InvalidArticleModeloException(String.format(MESSAGE_TOO_SHORT, minimumLength));
  }
}

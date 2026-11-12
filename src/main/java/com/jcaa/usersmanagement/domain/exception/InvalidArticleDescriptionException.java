package com.jcaa.usersmanagement.domain.exception;

public final class InvalidArticleDescriptionException extends DomainException {

  private static final String MESSAGE_EMPTY = "The article description must not be empty.";
  private static final String MESSAGE_TOO_SHORT =
      "The article description must have at least %d characters.";

  private InvalidArticleDescriptionException(final String message) {
    super(message);
  }

  public static InvalidArticleDescriptionException becauseValueIsEmpty() {
    return new InvalidArticleDescriptionException(MESSAGE_EMPTY);
  }

  public static InvalidArticleDescriptionException becauseLengthIsTooShort(final int minimumLength) {
    return new InvalidArticleDescriptionException(String.format(MESSAGE_TOO_SHORT, minimumLength));
  }
}

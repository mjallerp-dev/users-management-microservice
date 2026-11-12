package com.jcaa.usersmanagement.domain.exception;

public final class InvalidArticleIdException extends DomainException {

  private static final String MESSAGE_EMPTY = "The article id must not be empty.";
  private static final String MESSAGE_TOO_SHORT =
      "The article id must have at least %d characters.";

  private InvalidArticleIdException(final String message) {
    super(message);
  }

  public static InvalidArticleIdException becauseValueIsEmpty() {
    return new InvalidArticleIdException(MESSAGE_EMPTY);
  }

  public static InvalidArticleIdException becauseLengthIsTooShort(final int minimumLength) {
    return new InvalidArticleIdException(String.format(MESSAGE_TOO_SHORT, minimumLength));
  }
}

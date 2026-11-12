package com.jcaa.usersmanagement.domain.exception;

public final class InvalidArticleCategoryException extends DomainException {

  private static final String MESSAGE_INVALID = "The article category '%s' is not valid.";

  private InvalidArticleCategoryException(final String message) {
    super(message);
  }

  public static InvalidArticleCategoryException becauseValueIsInvalid(final String category) {
    return new InvalidArticleCategoryException(String.format(MESSAGE_INVALID, category));
  }
}

package com.jcaa.usersmanagement.domain.exception;

public final class ArticleAlreadyExistsException extends DomainException {

  private static final String MESSAGE_ID_EXISTS = "An article with id '%s' already exists.";

  private ArticleAlreadyExistsException(final String message) {
    super(message);
  }

  public static ArticleAlreadyExistsException becauseIdAlreadyExists(final String articleId) {
    return new ArticleAlreadyExistsException(String.format(MESSAGE_ID_EXISTS, articleId));
  }
}

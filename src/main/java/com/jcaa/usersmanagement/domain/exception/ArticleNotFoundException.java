package com.jcaa.usersmanagement.domain.exception;

public final class ArticleNotFoundException extends DomainException {

  private static final String MESSAGE_BY_ID = "The article with id '%s' was not found.";

  private ArticleNotFoundException(final String message) {
    super(message);
  }

  public static ArticleNotFoundException becauseIdWasNotFound(final String articleId) {
    return new ArticleNotFoundException(String.format(MESSAGE_BY_ID, articleId));
  }
}

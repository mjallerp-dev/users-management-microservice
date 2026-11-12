package com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception;

public final class PersistenceException extends RuntimeException {

  private static final String MESSAGE_SAVE = "Failed to save user with ID: '%s'.";
  private static final String MESSAGE_UPDATE = "Failed to update user with ID: '%s'.";
  private static final String MESSAGE_FIND = "Failed to find user with ID: '%s'.";
  private static final String MESSAGE_EMAIL = "Failed to find user with email: '%s'.";
  private static final String MESSAGE_ALL = "Failed to retrieve all users.";
  private static final String MESSAGE_DELETE = "Failed to delete user with ID: '%s'.";
  private static final String MESSAGE_CONNECTION = "Could not establish database connection.";
  private static final String MESSAGE_ARTICLE_SAVE = "Failed to save article with ID: '%s'.";
  private static final String MESSAGE_ARTICLE_UPDATE = "Failed to update article with ID: '%s'.";
  private static final String MESSAGE_ARTICLE_FIND = "Failed to find article with ID: '%s'.";
  private static final String MESSAGE_ARTICLE_ALL = "Failed to retrieve all articles.";
  private static final String MESSAGE_ARTICLE_DELETE = "Failed to delete article with ID: '%s'.";

  private PersistenceException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public static PersistenceException becauseSaveFailed(final String userId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_SAVE, userId), cause);
  }

  public static PersistenceException becauseUpdateFailed(
      final String userId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_UPDATE, userId), cause);
  }

  public static PersistenceException becauseFindByIdFailed(
      final String userId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_FIND, userId), cause);
  }

  public static PersistenceException becauseFindByEmailFailed(
      final String email, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_EMAIL, email), cause);
  }

  public static PersistenceException becauseFindAllFailed(final Throwable cause) {
    return new PersistenceException(MESSAGE_ALL, cause);
  }

  public static PersistenceException becauseDeleteFailed(
      final String userId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_DELETE, userId), cause);
  }

  public static PersistenceException becauseConnectionFailed(final Throwable cause) {
    return new PersistenceException(MESSAGE_CONNECTION, cause);
  }

  public static PersistenceException becauseArticleSaveFailed(
      final String articleId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_ARTICLE_SAVE, articleId), cause);
  }

  public static PersistenceException becauseArticleUpdateFailed(
      final String articleId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_ARTICLE_UPDATE, articleId), cause);
  }

  public static PersistenceException becauseArticleFindByIdFailed(
      final String articleId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_ARTICLE_FIND, articleId), cause);
  }

  public static PersistenceException becauseArticleFindAllFailed(final Throwable cause) {
    return new PersistenceException(MESSAGE_ARTICLE_ALL, cause);
  }

  public static PersistenceException becauseArticleDeleteFailed(
      final String articleId, final Throwable cause) {
    return new PersistenceException(String.format(MESSAGE_ARTICLE_DELETE, articleId), cause);
  }
}

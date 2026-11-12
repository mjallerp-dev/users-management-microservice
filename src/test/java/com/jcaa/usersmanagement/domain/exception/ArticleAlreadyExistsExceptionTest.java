package com.jcaa.usersmanagement.domain.exception;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ArticleAlreadyExistsException")
class ArticleAlreadyExistsExceptionTest {

  @Test
  @DisplayName("becauseIdAlreadyExists() debe incluir el valor recibido en el mensaje")
  void becauseIdAlreadyExistsShouldIncludeValueInMessage() {
    // Arrange
    final String articleId = "article-409";

    // Act
    final String message = ArticleAlreadyExistsException.becauseIdAlreadyExists(articleId).getMessage();

    // Assert
    assertTrue(message.contains(articleId), "el mensaje debe incluir el valor recibido");
  }

  @Test
  @DisplayName("debe ser una excepción de dominio")
  void shouldBeDomainException() {
    // Act
    final ArticleAlreadyExistsException exception = ArticleAlreadyExistsException.becauseIdAlreadyExists("article-409");

    // Assert
    assertInstanceOf(DomainException.class, exception);
  }
}

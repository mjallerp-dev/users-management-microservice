package com.jcaa.usersmanagement.domain.exception;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ArticleNotFoundException")
class ArticleNotFoundExceptionTest {

  @Test
  @DisplayName("becauseIdWasNotFound() debe incluir el valor recibido en el mensaje")
  void becauseIdWasNotFoundShouldIncludeValueInMessage() {
    // Arrange
    final String articleId = "article-404";

    // Act
    final String message = ArticleNotFoundException.becauseIdWasNotFound(articleId).getMessage();

    // Assert
    assertTrue(message.contains(articleId), "el mensaje debe incluir el valor recibido");
  }

  @Test
  @DisplayName("debe ser una excepción de dominio")
  void shouldBeDomainException() {
    // Act
    final ArticleNotFoundException exception = ArticleNotFoundException.becauseIdWasNotFound("article-404");

    // Assert
    assertInstanceOf(DomainException.class, exception);
  }
}

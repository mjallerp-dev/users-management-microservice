package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.DeleteArticlePort;
import com.jcaa.usersmanagement.application.port.out.GetArticleByIdPort;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteArticleCommand;
import com.jcaa.usersmanagement.domain.exception.ArticleNotFoundException;
import com.jcaa.usersmanagement.domain.model.ArticleModel;
import com.jcaa.usersmanagement.domain.valueobject.ArticleId;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para DeleteArticleService.
 *
 * <p>Cubre: validación del command, artículo inexistente y flujo feliz.
 */
@DisplayName("DeleteArticleService")
@ExtendWith(MockitoExtension.class)
class DeleteArticleServiceTest {

  @Mock private DeleteArticlePort deleteArticlePort;
  @Mock private GetArticleByIdPort getArticleByIdPort;
  @Mock private ArticleModel existingArticle;

  private DeleteArticleService service;

  @BeforeEach
  void setUp() {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      service =
          new DeleteArticleService(
              deleteArticlePort, getArticleByIdPort, validatorFactory.getValidator());
    }
  }

  // ── flujo feliz

  @Test
  @DisplayName("execute() elimina el artículo cuando existe")
  void shouldDeleteArticleWhenItExists() {
    // Arrange
    final DeleteArticleCommand command = new DeleteArticleCommand("a-01");

    when(getArticleByIdPort.getById(any())).thenReturn(Optional.of(existingArticle));

    // Act
    service.execute(command);

    // Assert
    verify(deleteArticlePort).delete(new ArticleId("a-01"));
  }

  // ── artículo inexistente

  @Test
  @DisplayName("execute() lanza ArticleNotFoundException cuando el artículo no existe")
  void shouldThrowWhenArticleDoesNotExist() {
    // Arrange
    final DeleteArticleCommand command = new DeleteArticleCommand("a-404");

    when(getArticleByIdPort.getById(any())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(ArticleNotFoundException.class, () -> service.execute(command));
    verify(deleteArticlePort, never()).delete(any());
  }

  // ── validación del command

  @Test
  @DisplayName("execute() lanza ConstraintViolationException cuando el id está en blanco")
  void shouldThrowWhenIdIsBlank() {
    // Arrange
    final DeleteArticleCommand command = new DeleteArticleCommand("  ");

    // Act & Assert
    assertThrows(ConstraintViolationException.class, () -> service.execute(command));
    verifyNoInteractions(deleteArticlePort, getArticleByIdPort);
  }
}

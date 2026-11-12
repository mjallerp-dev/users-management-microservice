package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetArticleByIdPort;
import com.jcaa.usersmanagement.application.service.dto.query.GetArticleByIdQuery;
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
 * Tests para GetArticleByIdService.
 *
 * <p>Cubre: validación del query, artículo inexistente y flujo feliz.
 */
@DisplayName("GetArticleByIdService")
@ExtendWith(MockitoExtension.class)
class GetArticleByIdServiceTest {

  @Mock private GetArticleByIdPort getArticleByIdPort;
  @Mock private ArticleModel article;

  private GetArticleByIdService service;

  @BeforeEach
  void setUp() {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      service = new GetArticleByIdService(getArticleByIdPort, validatorFactory.getValidator());
    }
  }

  // ── flujo feliz

  @Test
  @DisplayName("execute() retorna el artículo cuando existe")
  void shouldReturnArticleWhenItExists() {
    // Arrange
    when(getArticleByIdPort.getById(new ArticleId("a-01"))).thenReturn(Optional.of(article));

    // Act
    final ArticleModel result = service.execute(new GetArticleByIdQuery("a-01"));

    // Assert
    assertSame(article, result, "debe ser el mismo objeto del puerto");
  }

  // ── artículo inexistente

  @Test
  @DisplayName("execute() lanza ArticleNotFoundException cuando el artículo no existe")
  void shouldThrowWhenArticleDoesNotExist() {
    // Arrange
    when(getArticleByIdPort.getById(any())).thenReturn(Optional.empty());
    final GetArticleByIdQuery query = new GetArticleByIdQuery("a-404");

    // Act & Assert
    final ArticleNotFoundException exception =
        assertThrows(ArticleNotFoundException.class, () -> service.execute(query));
    assertTrue(exception.getMessage().contains("a-404"), "el mensaje debe incluir el id");
  }

  // ── validación del query

  @Test
  @DisplayName("execute() lanza ConstraintViolationException cuando el id está en blanco")
  void shouldThrowWhenIdIsBlank() {
    // Arrange
    final GetArticleByIdQuery query = new GetArticleByIdQuery("");

    // Act & Assert
    assertThrows(ConstraintViolationException.class, () -> service.execute(query));
    verifyNoInteractions(getArticleByIdPort);
  }
}

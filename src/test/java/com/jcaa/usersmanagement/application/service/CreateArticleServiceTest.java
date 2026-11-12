package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetArticleByIdPort;
import com.jcaa.usersmanagement.application.port.out.SaveArticlePort;
import com.jcaa.usersmanagement.application.service.dto.command.CreateArticleCommand;
import com.jcaa.usersmanagement.application.service.mapper.ArticleApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.ArticleAlreadyExistsException;
import com.jcaa.usersmanagement.domain.exception.InvalidArticleCategoryException;
import com.jcaa.usersmanagement.domain.model.ArticleModel;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para CreateArticleService.
 *
 * <p>Cubre: validación del command, categoría inválida, id duplicado y flujo feliz.
 */
@DisplayName("CreateArticleService")
@ExtendWith(MockitoExtension.class)
class CreateArticleServiceTest {

  @Mock private SaveArticlePort saveArticlePort;
  @Mock private GetArticleByIdPort getArticleByIdPort;

  private CreateArticleService service;

  @BeforeEach
  void setUp() {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      service =
          new CreateArticleService(
              saveArticlePort, getArticleByIdPort, validatorFactory.getValidator());
    }
  }

  private static CreateArticleCommand validCommand(final String id, final String category) {
    return new CreateArticleCommand(
        id,
        "Portátil 14 pulgadas",
        "Proveedor Demo",
        "Lenovo",
        "ThinkPad E14",
        category,
        "Tienda Centro",
        5,
        new BigDecimal("2500000.50"),
        new BigDecimal("3200000"),
        new BigDecimal("19"));
  }

  // ── flujo feliz

  @Test
  @DisplayName("execute() guarda el artículo cuando el id no existe")
  void shouldSaveArticleWhenIdIsNew() {
    // Arrange
    final CreateArticleCommand command = validCommand("a-01", "electronica");
    final ArticleModel savedArticle = ArticleApplicationMapper.fromCreateCommandToModel(command);

    when(getArticleByIdPort.getById(any())).thenReturn(Optional.empty());
    when(saveArticlePort.save(any())).thenReturn(savedArticle);

    // Act
    final ArticleModel result = service.execute(command);

    // Assert
    assertAll(
        "flujo feliz de CreateArticleService",
        () -> assertSame(savedArticle, result, "debe retornar el artículo devuelto por el puerto"),
        () -> assertEquals("a-01", result.getId().value(), "id del artículo guardado"));

    verify(saveArticlePort).save(savedArticle);
  }

  // ── id duplicado

  @Test
  @DisplayName("execute() lanza ArticleAlreadyExistsException cuando el id ya está registrado")
  void shouldThrowWhenIdAlreadyExists() {
    // Arrange
    final CreateArticleCommand command = validCommand("a-02", "hogar");
    final ArticleModel existing = ArticleApplicationMapper.fromCreateCommandToModel(command);

    when(getArticleByIdPort.getById(any())).thenReturn(Optional.of(existing));

    // Act & Assert
    assertThrows(ArticleAlreadyExistsException.class, () -> service.execute(command));
    verify(saveArticlePort, never()).save(any());
  }

  // ── categoría inválida

  @Test
  @DisplayName("execute() lanza InvalidArticleCategoryException cuando la categoría no existe")
  void shouldThrowWhenCategoryIsInvalid() {
    // Arrange
    final CreateArticleCommand command = validCommand("a-03", "juguetes");

    // Act & Assert
    assertThrows(InvalidArticleCategoryException.class, () -> service.execute(command));
    verifyNoInteractions(saveArticlePort, getArticleByIdPort);
  }

  // ── validación del command

  @Test
  @DisplayName(
      "execute() lanza ConstraintViolationException cuando el command tiene campos inválidos")
  void shouldThrowWhenCommandIsInvalid() {
    // Arrange — id en blanco, textos cortos, cantidad negativa, precio cero e IVA > 100
    final CreateArticleCommand command =
        new CreateArticleCommand(
            "",
            "TV",
            "AB",
            "LG",
            "X1",
            "",
            "T1",
            -1,
            BigDecimal.ZERO,
            null,
            new BigDecimal("150"));

    // Act & Assert
    assertThrows(ConstraintViolationException.class, () -> service.execute(command));
    verifyNoInteractions(saveArticlePort, getArticleByIdPort);
  }
}

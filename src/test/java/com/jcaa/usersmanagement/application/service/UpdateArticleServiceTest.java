package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetArticleByIdPort;
import com.jcaa.usersmanagement.application.port.out.UpdateArticlePort;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateArticleCommand;
import com.jcaa.usersmanagement.application.service.mapper.ArticleApplicationMapper;
import com.jcaa.usersmanagement.domain.enums.ArticleCategory;
import com.jcaa.usersmanagement.domain.exception.ArticleNotFoundException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para UpdateArticleService.
 *
 * <p>Cubre: validación del command, artículo inexistente y flujo feliz.
 */
@DisplayName("UpdateArticleService")
@ExtendWith(MockitoExtension.class)
class UpdateArticleServiceTest {

  @Mock private UpdateArticlePort updateArticlePort;
  @Mock private GetArticleByIdPort getArticleByIdPort;

  private UpdateArticleService service;

  @BeforeEach
  void setUp() {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      service =
          new UpdateArticleService(
              updateArticlePort, getArticleByIdPort, validatorFactory.getValidator());
    }
  }

  private static UpdateArticleCommand validCommand(final String id, final String description) {
    return new UpdateArticleCommand(
        id,
        description,
        "Proveedor Demo",
        "Lenovo",
        "ThinkPad E14",
        "electronica",
        "Tienda Centro",
        5,
        new BigDecimal("2500000.50"),
        new BigDecimal("3200000"),
        new BigDecimal("19"));
  }

  // ── flujo feliz

  @Test
  @DisplayName("execute() actualiza el artículo con los datos del command cuando existe")
  void shouldUpdateArticleWhenItExists() {
    // Arrange
    final ArticleModel current =
        ArticleApplicationMapper.fromUpdateCommandToModel(validCommand("a-01", "Portátil viejo"));
    final UpdateArticleCommand command = validCommand("a-01", "Portátil nuevo");

    when(getArticleByIdPort.getById(any())).thenReturn(Optional.of(current));
    when(updateArticlePort.update(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    final ArticleModel result = service.execute(command);

    // Assert
    final ArgumentCaptor<ArticleModel> captor = ArgumentCaptor.forClass(ArticleModel.class);
    verify(updateArticlePort).update(captor.capture());
    assertAll(
        "flujo feliz de UpdateArticleService",
        () -> assertEquals("a-01", captor.getValue().getId().value(), "id"),
        () ->
            assertEquals(
                "Portátil nuevo",
                captor.getValue().getDescription().value(),
                "debe enviar la nueva descripción al puerto"),
        () ->
            assertEquals(
                ArticleCategory.ELECTRONICA, captor.getValue().getCategory(), "category"),
        () -> assertSame(captor.getValue(), result, "debe retornar lo devuelto por el puerto"));
  }

  // ── artículo inexistente

  @Test
  @DisplayName("execute() lanza ArticleNotFoundException cuando el artículo no existe")
  void shouldThrowWhenArticleDoesNotExist() {
    // Arrange
    final UpdateArticleCommand command = validCommand("a-404", "Portátil nuevo");

    when(getArticleByIdPort.getById(any())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(ArticleNotFoundException.class, () -> service.execute(command));
    verify(updateArticlePort, never()).update(any());
  }

  // ── validación del command

  @Test
  @DisplayName(
      "execute() lanza ConstraintViolationException cuando el command tiene campos inválidos")
  void shouldThrowWhenCommandIsInvalid() {
    // Arrange — id en blanco y descripción demasiado corta
    final UpdateArticleCommand command = validCommand("", "TV");

    // Act & Assert
    assertThrows(ConstraintViolationException.class, () -> service.execute(command));
    verifyNoInteractions(updateArticlePort, getArticleByIdPort);
  }
}

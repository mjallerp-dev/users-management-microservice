package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidArticleModeloException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArticleModeloTest {

  // --- Happy Path Tests ---

  @ParameterizedTest
  @ValueSource(strings = {"ThinkPad E14", "   ThinkPad E14   ", "ThinkPad E14 \t"})
  @DisplayName("Valida que el valor se normalice eliminando espacios externos")
  void shouldNormalizeValue(final String rawValue) {
    // Act
    final ArticleModelo valueObject = new ArticleModelo(rawValue);

    // Assert
    assertEquals("ThinkPad E14", valueObject.value());
    assertEquals("ThinkPad E14", valueObject.toString());
  }

  @Test
  @DisplayName("Valida que dos objetos con el mismo valor sean iguales")
  void shouldBeEqualWhenValuesAreEqual() {
    assertEquals(new ArticleModelo("ThinkPad E14"), new ArticleModelo("  ThinkPad E14  "));
  }

  // --- Flujo con excepciones y ramas de validación ---

  @Test
  @DisplayName("Valida que el valor no sea nulo")
  void shouldValidateValueIsNotNull() {
    assertThrows(NullPointerException.class, () -> new ArticleModelo(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "\t", "X1", " S9 "})
  @DisplayName("Valida que el valor no sea vacío y tenga un tamaño mínimo")
  void shouldRejectInvalidValue(final String rawValue) {
    assertThrows(InvalidArticleModeloException.class, () -> new ArticleModelo(rawValue));
  }
}

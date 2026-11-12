package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidArticleDescriptionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArticleDescriptionTest {

  // --- Happy Path Tests ---

  @ParameterizedTest
  @ValueSource(strings = {"Portátil 14 pulgadas", "   Portátil 14 pulgadas   ", "Portátil 14 pulgadas \t"})
  @DisplayName("Valida que el valor se normalice eliminando espacios externos")
  void shouldNormalizeValue(final String rawValue) {
    // Act
    final ArticleDescription valueObject = new ArticleDescription(rawValue);

    // Assert
    assertEquals("Portátil 14 pulgadas", valueObject.value());
    assertEquals("Portátil 14 pulgadas", valueObject.toString());
  }

  @Test
  @DisplayName("Valida que dos objetos con el mismo valor sean iguales")
  void shouldBeEqualWhenValuesAreEqual() {
    assertEquals(new ArticleDescription("Portátil 14 pulgadas"), new ArticleDescription("  Portátil 14 pulgadas  "));
  }

  // --- Flujo con excepciones y ramas de validación ---

  @Test
  @DisplayName("Valida que el valor no sea nulo")
  void shouldValidateValueIsNotNull() {
    assertThrows(NullPointerException.class, () -> new ArticleDescription(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "\t", "Pc", "  TV  "})
  @DisplayName("Valida que el valor no sea vacío y tenga un tamaño mínimo")
  void shouldRejectInvalidValue(final String rawValue) {
    assertThrows(InvalidArticleDescriptionException.class, () -> new ArticleDescription(rawValue));
  }
}

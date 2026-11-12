package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidArticleIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArticleIdTest {

  // --- Happy Path Tests ---

  @ParameterizedTest
  @ValueSource(strings = {"a1b2c3d4-0000-4000-8000-000000000001", "   a1b2c3d4-0000-4000-8000-000000000001   ", "a1b2c3d4-0000-4000-8000-000000000001 \t"})
  @DisplayName("Valida que el valor se normalice eliminando espacios externos")
  void shouldNormalizeValue(final String rawValue) {
    // Act
    final ArticleId valueObject = new ArticleId(rawValue);

    // Assert
    assertEquals("a1b2c3d4-0000-4000-8000-000000000001", valueObject.value());
    assertEquals("a1b2c3d4-0000-4000-8000-000000000001", valueObject.toString());
  }

  @Test
  @DisplayName("Valida que dos objetos con el mismo valor sean iguales")
  void shouldBeEqualWhenValuesAreEqual() {
    assertEquals(new ArticleId("a1b2c3d4-0000-4000-8000-000000000001"), new ArticleId("  a1b2c3d4-0000-4000-8000-000000000001  "));
  }

  // --- Flujo con excepciones y ramas de validación ---

  @Test
  @DisplayName("Valida que el valor no sea nulo")
  void shouldValidateValueIsNotNull() {
    assertThrows(NullPointerException.class, () -> new ArticleId(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "\t", "\n"})
  @DisplayName("Valida que el valor no sea vacío")
  void shouldRejectInvalidValue(final String rawValue) {
    assertThrows(InvalidArticleIdException.class, () -> new ArticleId(rawValue));
  }
}

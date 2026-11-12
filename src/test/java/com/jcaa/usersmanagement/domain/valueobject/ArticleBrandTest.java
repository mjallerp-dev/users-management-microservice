package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidArticleBrandException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArticleBrandTest {

  // --- Happy Path Tests ---

  @ParameterizedTest
  @ValueSource(strings = {"Lenovo", "   Lenovo   ", "Lenovo \t"})
  @DisplayName("Valida que el valor se normalice eliminando espacios externos")
  void shouldNormalizeValue(final String rawValue) {
    // Act
    final ArticleBrand valueObject = new ArticleBrand(rawValue);

    // Assert
    assertEquals("Lenovo", valueObject.value());
    assertEquals("Lenovo", valueObject.toString());
  }

  @Test
  @DisplayName("Valida que dos objetos con el mismo valor sean iguales")
  void shouldBeEqualWhenValuesAreEqual() {
    assertEquals(new ArticleBrand("Lenovo"), new ArticleBrand("  Lenovo  "));
  }

  // --- Flujo con excepciones y ramas de validación ---

  @Test
  @DisplayName("Valida que el valor no sea nulo")
  void shouldValidateValueIsNotNull() {
    assertThrows(NullPointerException.class, () -> new ArticleBrand(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "\t", "LG", " HP "})
  @DisplayName("Valida que el valor no sea vacío y tenga un tamaño mínimo")
  void shouldRejectInvalidValue(final String rawValue) {
    assertThrows(InvalidArticleBrandException.class, () -> new ArticleBrand(rawValue));
  }
}

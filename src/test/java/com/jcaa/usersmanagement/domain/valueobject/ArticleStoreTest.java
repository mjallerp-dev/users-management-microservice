package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidArticleStoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArticleStoreTest {

  // --- Happy Path Tests ---

  @ParameterizedTest
  @ValueSource(strings = {"Tienda Centro", "   Tienda Centro   ", "Tienda Centro \t"})
  @DisplayName("Valida que el valor se normalice eliminando espacios externos")
  void shouldNormalizeValue(final String rawValue) {
    // Act
    final ArticleStore valueObject = new ArticleStore(rawValue);

    // Assert
    assertEquals("Tienda Centro", valueObject.value());
    assertEquals("Tienda Centro", valueObject.toString());
  }

  @Test
  @DisplayName("Valida que dos objetos con el mismo valor sean iguales")
  void shouldBeEqualWhenValuesAreEqual() {
    assertEquals(new ArticleStore("Tienda Centro"), new ArticleStore("  Tienda Centro  "));
  }

  // --- Flujo con excepciones y ramas de validación ---

  @Test
  @DisplayName("Valida que el valor no sea nulo")
  void shouldValidateValueIsNotNull() {
    assertThrows(NullPointerException.class, () -> new ArticleStore(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "\t", "T1", " Su"})
  @DisplayName("Valida que el valor no sea vacío y tenga un tamaño mínimo")
  void shouldRejectInvalidValue(final String rawValue) {
    assertThrows(InvalidArticleStoreException.class, () -> new ArticleStore(rawValue));
  }
}

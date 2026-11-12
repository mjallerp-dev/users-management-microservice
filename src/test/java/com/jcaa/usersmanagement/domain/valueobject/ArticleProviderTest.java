package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidArticleProviderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArticleProviderTest {

  // --- Happy Path Tests ---

  @ParameterizedTest
  @ValueSource(strings = {"Proveedor Demo", "   Proveedor Demo   ", "Proveedor Demo \t"})
  @DisplayName("Valida que el valor se normalice eliminando espacios externos")
  void shouldNormalizeValue(final String rawValue) {
    // Act
    final ArticleProvider valueObject = new ArticleProvider(rawValue);

    // Assert
    assertEquals("Proveedor Demo", valueObject.value());
    assertEquals("Proveedor Demo", valueObject.toString());
  }

  @Test
  @DisplayName("Valida que dos objetos con el mismo valor sean iguales")
  void shouldBeEqualWhenValuesAreEqual() {
    assertEquals(new ArticleProvider("Proveedor Demo"), new ArticleProvider("  Proveedor Demo  "));
  }

  // --- Flujo con excepciones y ramas de validación ---

  @Test
  @DisplayName("Valida que el valor no sea nulo")
  void shouldValidateValueIsNotNull() {
    assertThrows(NullPointerException.class, () -> new ArticleProvider(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "\t", "AB", " Xy "})
  @DisplayName("Valida que el valor no sea vacío y tenga un tamaño mínimo")
  void shouldRejectInvalidValue(final String rawValue) {
    assertThrows(InvalidArticleProviderException.class, () -> new ArticleProvider(rawValue));
  }
}

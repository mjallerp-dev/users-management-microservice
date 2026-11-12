package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidArticleQuantityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArticleQuantityTest {

  // --- Happy Path Tests ---

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 5, 1000})
  @DisplayName("Valida que la cantidad acepte cero y valores positivos")
  void shouldAcceptZeroAndPositiveValues(final int quantity) {
    // Act
    final ArticleQuantity valueObject = new ArticleQuantity(quantity);

    // Assert
    assertEquals(quantity, valueObject.value());
    assertEquals(String.valueOf(quantity), valueObject.toString());
  }

  // --- Flujo con excepciones y ramas de validación ---

  @Test
  @DisplayName("Valida que la cantidad no sea vacía")
  void shouldRejectNullValue() {
    assertThrows(InvalidArticleQuantityException.class, () -> new ArticleQuantity(null));
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, -100})
  @DisplayName("Valida que la cantidad no sea negativa")
  void shouldRejectNegativeValues(final int quantity) {
    assertThrows(InvalidArticleQuantityException.class, () -> new ArticleQuantity(quantity));
  }
}

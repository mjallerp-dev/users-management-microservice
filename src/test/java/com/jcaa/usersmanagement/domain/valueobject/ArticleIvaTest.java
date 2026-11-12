package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidArticleIvaException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ArticleIvaTest {

  // --- Happy Path Tests ---

  @ParameterizedTest
  @CsvSource({"0, 0.00", "5, 5.00", "19, 19.00", "8.5, 8.50", "100, 100.00"})
  @DisplayName("Valida que el IVA acepte valores entre 0 y 100 con dos decimales")
  void shouldAcceptValuesBetweenZeroAndOneHundred(final String rawValue, final String expected) {
    // Act
    final ArticleIva valueObject = new ArticleIva(new BigDecimal(rawValue));

    // Assert
    assertEquals(new BigDecimal(expected), valueObject.value());
    assertEquals(expected, valueObject.toString());
  }

  // --- Flujo con excepciones y ramas de validación ---

  @Test
  @DisplayName("Valida que el IVA no sea vacío")
  void shouldRejectNullValue() {
    assertThrows(InvalidArticleIvaException.class, () -> new ArticleIva(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"-0.01", "-1", "-19"})
  @DisplayName("Valida que el IVA no sea negativo")
  void shouldRejectNegativeValues(final String rawValue) {
    final BigDecimal value = new BigDecimal(rawValue);
    final InvalidArticleIvaException exception =
        assertThrows(InvalidArticleIvaException.class, () -> new ArticleIva(value));
    assertEquals(
        InvalidArticleIvaException.becauseMustBeGreaterThanOrEqualToZero().getMessage(),
        exception.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {"100.01", "101", "150"})
  @DisplayName("Valida que el IVA no supere 100")
  void shouldRejectValuesAboveOneHundred(final String rawValue) {
    final BigDecimal value = new BigDecimal(rawValue);
    final InvalidArticleIvaException exception =
        assertThrows(InvalidArticleIvaException.class, () -> new ArticleIva(value));
    assertEquals(
        InvalidArticleIvaException.becauseMustBeLessThanOrEqualToOneHundred().getMessage(),
        exception.getMessage());
  }
}

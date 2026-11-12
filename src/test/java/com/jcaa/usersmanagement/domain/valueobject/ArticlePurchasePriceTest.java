package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidArticlePurchasePriceException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ArticlePurchasePriceTest {

  // --- Happy Path Tests ---

  @ParameterizedTest
  @CsvSource({"2500000.5, 2500000.50", "0.01, 0.01", "10, 10.00", "19.995, 20.00"})
  @DisplayName("Valida que el precio se normalice a dos decimales")
  void shouldNormalizeToTwoDecimals(final String rawValue, final String expected) {
    // Act
    final ArticlePurchasePrice valueObject = new ArticlePurchasePrice(new BigDecimal(rawValue));

    // Assert
    assertEquals(new BigDecimal(expected), valueObject.value());
    assertEquals(expected, valueObject.toString());
  }

  @Test
  @DisplayName("Valida que dos precios equivalentes sean iguales")
  void shouldBeEqualWhenValuesAreEquivalent() {
    assertEquals(new ArticlePurchasePrice(new BigDecimal("10")), new ArticlePurchasePrice(new BigDecimal("10.000")));
  }

  // --- Flujo con excepciones y ramas de validación ---

  @Test
  @DisplayName("Valida que el precio no sea vacío")
  void shouldRejectNullValue() {
    assertThrows(InvalidArticlePurchasePriceException.class, () -> new ArticlePurchasePrice(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "0.00", "0.004", "-1", "-0.01"})
  @DisplayName("Valida que el precio sea mayor que cero")
  void shouldRejectZeroOrNegativeValues(final String rawValue) {
    final BigDecimal value = new BigDecimal(rawValue);
    assertThrows(InvalidArticlePurchasePriceException.class, () -> new ArticlePurchasePrice(value));
  }
}

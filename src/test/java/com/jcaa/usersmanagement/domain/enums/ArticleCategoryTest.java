package com.jcaa.usersmanagement.domain.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jcaa.usersmanagement.domain.exception.InvalidArticleCategoryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

class ArticleCategoryTest {

  // --- Happy path ---

  @ParameterizedTest
  @CsvSource({
    "electronica, ELECTRONICA",
    "ropa, ROPA",
    "calzado, CALZADO",
    "alimentos, ALIMENTOS",
    "bebidas, BEBIDAS",
    "hogar, HOGAR",
    "belleza, BELLEZA",
    "deportes, DEPORTES",
    "papeleria, PAPELERIA",
    "ferreteria, FERRETERIA",
    "otros, OTROS"
  })
  @DisplayName("Debe devolver la categoría correspondiente al valor recibido")
  void shouldReturnCategoryForValidValue(final String value, final ArticleCategory expected) {
    assertEquals(expected, ArticleCategory.fromString(value));
  }

  @ParameterizedTest
  @ValueSource(strings = {"ELECTRONICA", "Electronica", "eLeCtRoNiCa"})
  @DisplayName("Debe ignorar mayúsculas y minúsculas")
  void shouldIgnoreCase(final String value) {
    assertEquals(ArticleCategory.ELECTRONICA, ArticleCategory.fromString(value));
  }

  @ParameterizedTest
  @EnumSource(ArticleCategory.class)
  @DisplayName("value() y toString() deben devolver el valor en minúscula que se persiste")
  void shouldExposeLowercaseValue(final ArticleCategory category) {
    final String expected = category.name().toLowerCase();
    assertEquals(expected, category.value());
    assertEquals(expected, category.toString());
  }

  @Test
  @DisplayName("Debe tener las mismas 11 categorías que el proyecto PHP")
  void shouldHaveElevenCategories() {
    assertEquals(11, ArticleCategory.values().length);
  }

  // --- Flujo con excepciones y ramas de validación ---

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\t", "\n", "juguetes", "electrónica", " ropa "})
  @DisplayName("Debe lanzar InvalidArticleCategoryException cuando el valor es vacío o inválido")
  void shouldThrowWhenValueIsEmptyOrInvalid(final String value) {
    assertThrows(InvalidArticleCategoryException.class, () -> ArticleCategory.fromString(value));
  }

  @Test
  @DisplayName("Debe lanzar InvalidArticleCategoryException cuando el valor es nulo")
  void shouldThrowWhenValueIsNull() {
    assertThrows(InvalidArticleCategoryException.class, () -> ArticleCategory.fromString(null));
  }
}

package com.jcaa.usersmanagement.domain.enums;

import com.jcaa.usersmanagement.domain.exception.InvalidArticleCategoryException;

public enum ArticleCategory {
  ELECTRONICA("electronica"),
  ROPA("ropa"),
  CALZADO("calzado"),
  ALIMENTOS("alimentos"),
  BEBIDAS("bebidas"),
  HOGAR("hogar"),
  BELLEZA("belleza"),
  DEPORTES("deportes"),
  PAPELERIA("papeleria"),
  FERRETERIA("ferreteria"),
  OTROS("otros");

  private final String value;

  ArticleCategory(final String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  public static ArticleCategory fromString(final String value) {
    for (final ArticleCategory category : values()) {
      if (category.value.equalsIgnoreCase(value)) {
        return category;
      }
    }
    throw InvalidArticleCategoryException.becauseValueIsInvalid(value);
  }

  @Override
  public String toString() {
    return value;
  }
}

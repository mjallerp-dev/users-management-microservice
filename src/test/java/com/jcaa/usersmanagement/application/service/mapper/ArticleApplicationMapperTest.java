package com.jcaa.usersmanagement.application.service.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.application.service.dto.command.CreateArticleCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteArticleCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateArticleCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetArticleByIdQuery;
import com.jcaa.usersmanagement.domain.enums.ArticleCategory;
import com.jcaa.usersmanagement.domain.exception.InvalidArticleCategoryException;
import com.jcaa.usersmanagement.domain.model.ArticleModel;
import com.jcaa.usersmanagement.domain.valueobject.ArticleId;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ArticleApplicationMapper")
class ArticleApplicationMapperTest {

  private static final String ID             = "a-001";
  private static final String DESCRIPTION    = "Portátil 14 pulgadas";
  private static final String PROVIDER       = "Proveedor Demo";
  private static final String BRAND          = "Lenovo";
  private static final String MODEL          = "ThinkPad E14";
  private static final String CATEGORY       = "electronica";
  private static final String STORE          = "Tienda Centro";
  private static final Integer QUANTITY      = 5;
  private static final BigDecimal PURCHASE_PRICE = new BigDecimal("2500000.5");
  private static final BigDecimal SALE_PRICE     = new BigDecimal("3200000");
  private static final BigDecimal IVA            = new BigDecimal("19");

  // ── fromCreateCommandToModel()

  @Test
  @DisplayName("fromCreateCommandToModel() mapea todos los campos del command")
  void shouldMapCreateCommandToModel() {
    // Arrange
    final CreateArticleCommand command =
        new CreateArticleCommand(
            ID, DESCRIPTION, PROVIDER, BRAND, MODEL, CATEGORY, STORE, QUANTITY, PURCHASE_PRICE,
            SALE_PRICE, IVA);

    // Act
    final ArticleModel result = ArticleApplicationMapper.fromCreateCommandToModel(command);

    // Assert
    assertAll(
        "fromCreateCommandToModel()",
        () -> assertEquals(ID,          result.getId().value(),          "id"),
        () -> assertEquals(DESCRIPTION, result.getDescription().value(), "description"),
        () -> assertEquals(PROVIDER,    result.getProvider().value(),    "provider"),
        () -> assertEquals(BRAND,       result.getBrand().value(),       "brand"),
        () -> assertEquals(MODEL,       result.getModel().value(),       "model"),
        () -> assertEquals(ArticleCategory.ELECTRONICA, result.getCategory(), "category"),
        () -> assertEquals(STORE,       result.getStore().value(),       "store"),
        () -> assertEquals(QUANTITY,    result.getQuantity().value(),    "quantity"),
        () -> assertEquals(new BigDecimal("2500000.50"), result.getPurchasePrice().value(), "purchasePrice"),
        () -> assertEquals(new BigDecimal("3200000.00"), result.getSalePrice().value(),     "salePrice"),
        () -> assertEquals(new BigDecimal("19.00"),      result.getIva().value(),           "iva"));
  }

  // ── fromCreateCommandToModel() — categoría inválida

  @Test
  @DisplayName("fromCreateCommandToModel() lanza InvalidArticleCategoryException con categoría inválida")
  void shouldThrowWhenCreateCommandHasInvalidCategory() {
    // Arrange
    final CreateArticleCommand command =
        new CreateArticleCommand(
            ID, DESCRIPTION, PROVIDER, BRAND, MODEL, "juguetes", STORE, QUANTITY, PURCHASE_PRICE,
            SALE_PRICE, IVA);

    // Act & Assert
    assertThrows(
        InvalidArticleCategoryException.class,
        () -> ArticleApplicationMapper.fromCreateCommandToModel(command));
  }

  // ── fromUpdateCommandToModel()

  @Test
  @DisplayName("fromUpdateCommandToModel() mapea todos los campos del command")
  void shouldMapUpdateCommandToModel() {
    // Arrange
    final UpdateArticleCommand command =
        new UpdateArticleCommand(
            ID, DESCRIPTION, PROVIDER, BRAND, MODEL, "HOGAR", STORE, 0, PURCHASE_PRICE,
            SALE_PRICE, new BigDecimal("5"));

    // Act
    final ArticleModel result = ArticleApplicationMapper.fromUpdateCommandToModel(command);

    // Assert
    assertAll(
        "fromUpdateCommandToModel()",
        () -> assertEquals(ID,          result.getId().value(),          "id"),
        () -> assertEquals(DESCRIPTION, result.getDescription().value(), "description"),
        () -> assertEquals(PROVIDER,    result.getProvider().value(),    "provider"),
        () -> assertEquals(BRAND,       result.getBrand().value(),       "brand"),
        () -> assertEquals(MODEL,       result.getModel().value(),       "model"),
        () -> assertEquals(ArticleCategory.HOGAR, result.getCategory(),  "category"),
        () -> assertEquals(STORE,       result.getStore().value(),       "store"),
        () -> assertEquals(0,           result.getQuantity().value(),    "quantity"),
        () -> assertEquals(new BigDecimal("2500000.50"), result.getPurchasePrice().value(), "purchasePrice"),
        () -> assertEquals(new BigDecimal("3200000.00"), result.getSalePrice().value(),     "salePrice"),
        () -> assertEquals(new BigDecimal("5.00"),       result.getIva().value(),           "iva"));
  }

  // ── fromGetArticleByIdQueryToArticleId()

  @Test
  @DisplayName("fromGetArticleByIdQueryToArticleId() extrae el ArticleId del query")
  void shouldExtractArticleIdFromQuery() {
    // Arrange
    final GetArticleByIdQuery query = new GetArticleByIdQuery(ID);

    // Act
    final ArticleId result = ArticleApplicationMapper.fromGetArticleByIdQueryToArticleId(query);

    // Assert
    assertEquals(ID, result.value(), "id debe coincidir con el del query");
  }

  // ── fromDeleteCommandToArticleId()

  @Test
  @DisplayName("fromDeleteCommandToArticleId() extrae el ArticleId del command")
  void shouldExtractArticleIdFromDeleteCommand() {
    // Arrange
    final DeleteArticleCommand command = new DeleteArticleCommand(ID);

    // Act
    final ArticleId result = ArticleApplicationMapper.fromDeleteCommandToArticleId(command);

    // Assert
    assertEquals(ID, result.value(), "id debe coincidir con el del command");
  }
}

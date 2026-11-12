package com.jcaa.usersmanagement.infrastructure.adapter.persistence.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.domain.enums.ArticleCategory;
import com.jcaa.usersmanagement.domain.exception.InvalidArticleCategoryException;
import com.jcaa.usersmanagement.domain.model.ArticleModel;
import com.jcaa.usersmanagement.domain.valueobject.ArticleBrand;
import com.jcaa.usersmanagement.domain.valueobject.ArticleDescription;
import com.jcaa.usersmanagement.domain.valueobject.ArticleId;
import com.jcaa.usersmanagement.domain.valueobject.ArticleIva;
import com.jcaa.usersmanagement.domain.valueobject.ArticleModelo;
import com.jcaa.usersmanagement.domain.valueobject.ArticleProvider;
import com.jcaa.usersmanagement.domain.valueobject.ArticlePurchasePrice;
import com.jcaa.usersmanagement.domain.valueobject.ArticleQuantity;
import com.jcaa.usersmanagement.domain.valueobject.ArticleSalePrice;
import com.jcaa.usersmanagement.domain.valueobject.ArticleStore;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.dto.ArticlePersistenceDto;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.entity.ArticleEntity;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for ArticlePersistenceMapper.
 *
 * <p>Covers: fromModelToDto(), fromEntityToModel() (including an unknown category),
 * fromResultSetToEntity() (happy path and SQLException propagation), and
 * fromResultSetToModelList() — empty list, multiple rows, and SQLException propagation during
 * iteration.
 */
@DisplayName("ArticlePersistenceMapper")
@ExtendWith(MockitoExtension.class)
class ArticlePersistenceMapperTest {

  private static final String ID = "a-001";
  private static final String DESCRIPTION = "Portátil 14 pulgadas";
  private static final String PROVIDER = "Proveedor Demo";
  private static final String BRAND = "Lenovo";
  private static final String MODEL = "ThinkPad E14";
  private static final String CATEGORY = "electronica";
  private static final String STORE = "Tienda Centro";
  private static final int QUANTITY = 5;
  private static final BigDecimal PURCHASE_PRICE = new BigDecimal("2500000.50");
  private static final BigDecimal SALE_PRICE = new BigDecimal("3200000.00");
  private static final BigDecimal IVA = new BigDecimal("19.00");
  private static final String CREATED_AT = "2024-01-01 00:00:00";
  private static final String UPDATED_AT = "2024-01-02 00:00:00";

  @Mock private ResultSet resultSet;

  private ArticleModel articleModel;
  private ArticleEntity articleEntity;

  @BeforeEach
  void setUp() {
    articleModel =
        new ArticleModel(
            new ArticleId(ID),
            new ArticleDescription(DESCRIPTION),
            new ArticleProvider(PROVIDER),
            new ArticleBrand(BRAND),
            new ArticleModelo(MODEL),
            ArticleCategory.ELECTRONICA,
            new ArticleStore(STORE),
            new ArticleQuantity(QUANTITY),
            new ArticlePurchasePrice(PURCHASE_PRICE),
            new ArticleSalePrice(SALE_PRICE),
            new ArticleIva(IVA));

    articleEntity =
        new ArticleEntity(
            ID, DESCRIPTION, PROVIDER, BRAND, MODEL, CATEGORY, STORE, QUANTITY, PURCHASE_PRICE,
            SALE_PRICE, IVA, CREATED_AT, UPDATED_AT);
  }

  // ── fromModelToDto()

  @Test
  @DisplayName("fromModelToDto() maps all ArticleModel fields and sets null timestamps")
  void shouldMapModelToDto() {
    // Act
    final ArticlePersistenceDto result = ArticlePersistenceMapper.fromModelToDto(articleModel);

    // Assert
    assertAll(
        "fromModelToDto()",
        () -> assertEquals(ID, result.id(), "id"),
        () -> assertEquals(DESCRIPTION, result.description(), "description"),
        () -> assertEquals(PROVIDER, result.provider(), "provider"),
        () -> assertEquals(BRAND, result.brand(), "brand"),
        () -> assertEquals(MODEL, result.model(), "model"),
        () -> assertEquals(CATEGORY, result.category(), "category must be the lowercase value"),
        () -> assertEquals(STORE, result.store(), "store"),
        () -> assertEquals(QUANTITY, result.quantity(), "quantity"),
        () -> assertEquals(PURCHASE_PRICE, result.purchasePrice(), "purchasePrice"),
        () -> assertEquals(SALE_PRICE, result.salePrice(), "salePrice"),
        () -> assertEquals(IVA, result.iva(), "iva"),
        () -> assertNull(result.createdAt(), "createdAt must be null"),
        () -> assertNull(result.updatedAt(), "updatedAt must be null"));
  }

  // ── fromEntityToModel()

  @Test
  @DisplayName("fromEntityToModel() maps all ArticleEntity fields to a domain ArticleModel")
  void shouldMapEntityToModel() {
    // Act
    final ArticleModel result = ArticlePersistenceMapper.fromEntityToModel(articleEntity);

    // Assert
    assertEquals(articleModel, result, "all fields must match the expected model");
  }

  @Test
  @DisplayName("fromEntityToModel() throws InvalidArticleCategoryException for unknown category")
  void shouldThrowWhenEntityHasUnknownCategory() {
    // Arrange
    final ArticleEntity entity =
        new ArticleEntity(
            ID, DESCRIPTION, PROVIDER, BRAND, MODEL, "juguetes", STORE, QUANTITY, PURCHASE_PRICE,
            SALE_PRICE, IVA, CREATED_AT, UPDATED_AT);

    // Act + Assert
    assertThrows(
        InvalidArticleCategoryException.class,
        () -> ArticlePersistenceMapper.fromEntityToModel(entity));
  }

  // ── fromResultSetToEntity() — happy path

  @Test
  @DisplayName("fromResultSetToEntity() reads all thirteen columns from the ResultSet")
  void shouldReadAllColumnsFromResultSet() throws SQLException {
    // Arrange
    stubRow(ID);

    // Act
    final ArticleEntity result = ArticlePersistenceMapper.fromResultSetToEntity(resultSet);

    // Assert
    assertEquals(articleEntity, result, "all columns must be read into the entity");
  }

  // ── fromResultSetToEntity() — SQLException propagation

  @Test
  @DisplayName("fromResultSetToEntity() propagates SQLException when ResultSet read fails")
  void shouldPropagateExceptionFromResultSet() throws SQLException {
    // Arrange
    when(resultSet.getString(anyString())).thenThrow(new SQLException("Column read failed"));

    // Act + Assert
    assertThrows(
        SQLException.class,
        () -> ArticlePersistenceMapper.fromResultSetToEntity(resultSet),
        "must propagate SQLException when ResultSet throws on getString");
  }

  // ── fromResultSetToModelList() — empty

  @Test
  @DisplayName("fromResultSetToModelList() returns an empty list when ResultSet has no rows")
  void shouldReturnEmptyListWhenResultSetIsEmpty() throws SQLException {
    // Arrange
    when(resultSet.next()).thenReturn(false);

    // Act
    final List<ArticleModel> result = ArticlePersistenceMapper.fromResultSetToModelList(resultSet);

    // Assert
    assertTrue(result.isEmpty(), "must return an empty list when ResultSet has no rows");
  }

  // ── fromResultSetToModelList() — multiple rows

  @Test
  @DisplayName("fromResultSetToModelList() returns one model per row in the ResultSet")
  void shouldReturnOneModelPerRow() throws SQLException {
    // Arrange
    when(resultSet.next()).thenReturn(true, true, false);
    stubRow(ID);
    when(resultSet.getString("id")).thenReturn(ID, "a-002");

    // Act
    final List<ArticleModel> result = ArticlePersistenceMapper.fromResultSetToModelList(resultSet);

    // Assert
    assertAll(
        "fromResultSetToModelList()",
        () -> assertEquals(2, result.size(), "must return one model per row in the ResultSet"),
        () -> assertEquals(ID, result.get(0).getId().value(), "first row id"),
        () -> assertEquals("a-002", result.get(1).getId().value(), "second row id"));
  }

  // ── fromResultSetToModelList() — SQLException propagation during iteration

  @Test
  @DisplayName("fromResultSetToModelList() propagates SQLException when a row read fails")
  void shouldPropagateExceptionDuringIteration() throws SQLException {
    // Arrange
    when(resultSet.next()).thenReturn(true);
    when(resultSet.getString(anyString())).thenThrow(new SQLException("Row read failed"));

    // Act + Assert
    assertThrows(
        SQLException.class,
        () -> ArticlePersistenceMapper.fromResultSetToModelList(resultSet),
        "must propagate SQLException when a row fails to be read");
  }

  private void stubRow(final String id) throws SQLException {
    when(resultSet.getString("id")).thenReturn(id);
    when(resultSet.getString("description")).thenReturn(DESCRIPTION);
    when(resultSet.getString("provider")).thenReturn(PROVIDER);
    when(resultSet.getString("brand")).thenReturn(BRAND);
    when(resultSet.getString("model")).thenReturn(MODEL);
    when(resultSet.getString("category")).thenReturn(CATEGORY);
    when(resultSet.getString("store")).thenReturn(STORE);
    when(resultSet.getInt("quantity")).thenReturn(QUANTITY);
    when(resultSet.getBigDecimal("purchase_price")).thenReturn(PURCHASE_PRICE);
    when(resultSet.getBigDecimal("sale_price")).thenReturn(SALE_PRICE);
    when(resultSet.getBigDecimal("iva")).thenReturn(IVA);
    when(resultSet.getString("created_at")).thenReturn(CREATED_AT);
    when(resultSet.getString("updated_at")).thenReturn(UPDATED_AT);
  }
}

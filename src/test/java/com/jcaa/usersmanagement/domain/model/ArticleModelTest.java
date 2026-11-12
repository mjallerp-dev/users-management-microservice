package com.jcaa.usersmanagement.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.enums.ArticleCategory;
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
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ArticleModel")
class ArticleModelTest {

  // ── Arrange (variables globales para la mayoría de las test)
  private ArticleId id;
  private ArticleDescription description;
  private ArticleProvider provider;
  private ArticleBrand brand;
  private ArticleModelo model;
  private ArticleStore store;
  private ArticleQuantity quantity;
  private ArticlePurchasePrice purchasePrice;
  private ArticleSalePrice salePrice;
  private ArticleIva iva;

  @BeforeEach
  void setUp() {
    id = new ArticleId("a-001");
    description = new ArticleDescription("Portátil 14 pulgadas");
    provider = new ArticleProvider("Proveedor Demo");
    brand = new ArticleBrand("Lenovo");
    model = new ArticleModelo("ThinkPad E14");
    store = new ArticleStore("Tienda Centro");
    quantity = new ArticleQuantity(5);
    purchasePrice = new ArticlePurchasePrice(new BigDecimal("2500000.50"));
    salePrice = new ArticleSalePrice(new BigDecimal("3200000"));
    iva = new ArticleIva(new BigDecimal("19"));
  }

  // ── create()

  @Test
  @DisplayName("create() debe preservar todos los campos recibidos")
  void shouldCreateArticleAndPreserveAllFields() {
    // Act
    final ArticleModel article =
        ArticleModel.create(
            id,
            description,
            provider,
            brand,
            model,
            ArticleCategory.ELECTRONICA,
            store,
            quantity,
            purchasePrice,
            salePrice,
            iva);

    // Assert
    assertAll(
        "create() factory",
        () -> assertSame(id, article.getId(), "id debe preservarse"),
        () -> assertSame(description, article.getDescription(), "description debe preservarse"),
        () -> assertSame(provider, article.getProvider(), "provider debe preservarse"),
        () -> assertSame(brand, article.getBrand(), "brand debe preservarse"),
        () -> assertSame(model, article.getModel(), "model debe preservarse"),
        () ->
            assertEquals(
                ArticleCategory.ELECTRONICA, article.getCategory(), "category debe preservarse"),
        () -> assertSame(store, article.getStore(), "store debe preservarse"),
        () -> assertSame(quantity, article.getQuantity(), "quantity debe preservarse"),
        () ->
            assertSame(purchasePrice, article.getPurchasePrice(), "purchasePrice debe preservarse"),
        () -> assertSame(salePrice, article.getSalePrice(), "salePrice debe preservarse"),
        () -> assertSame(iva, article.getIva(), "iva debe preservarse"));
  }
}

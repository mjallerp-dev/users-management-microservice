package com.jcaa.usersmanagement.domain.event;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.enums.ArticleCategory;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test de ArticleUpdatedDomainEvent")
class ArticleUpdatedDomainEventTest {

  // ── Arrange Generales
  private static final String ID = "article-001";
  private static final String DESCRIPTION = "Portátil 14 pulgadas";
  private static final String PROVIDER = "Proveedor Demo";
  private static final String BRAND = "Lenovo";
  private static final String MODEL = "ThinkPad E14";
  private static final String STORE = "Tienda Centro";

  private ArticleModel article;

  @BeforeEach
  void setUp() {
    article =
        new ArticleModel(
            new ArticleId(ID),
            new ArticleDescription(DESCRIPTION),
            new ArticleProvider(PROVIDER),
            new ArticleBrand(BRAND),
            new ArticleModelo(MODEL),
            ArticleCategory.ELECTRONICA,
            new ArticleStore(STORE),
            new ArticleQuantity(5),
            new ArticlePurchasePrice(new BigDecimal("2500000.5")),
            new ArticleSalePrice(new BigDecimal("3200000")),
            new ArticleIva(new BigDecimal("19")));
  }

  // ── eventName

  @Test
  @DisplayName("eventName() debe retornar la constante 'article.updated'")
  void shouldHaveEventNameArticleUpdated() {
    // Arrange
    final ArticleUpdatedDomainEvent event = new ArticleUpdatedDomainEvent(article);

    // Act
    final String result = event.getEventName();

    // Assert
    assertEquals("article.updated", result);
  }

  // ── occurredOn

  @Test
  @DisplayName("occurredOn() no debe ser nulo y debe quedar acotado al instante de construcción")
  void shouldRecordOccurredOnAtCreationTime() {
    // Arrange
    final LocalDateTime before = LocalDateTime.now();
    final ArticleUpdatedDomainEvent event = new ArticleUpdatedDomainEvent(article);
    final LocalDateTime after = LocalDateTime.now();

    // Act
    final LocalDateTime occurredOn = event.getOccurredOn();

    // Assert
    assertNotNull(occurredOn, "occurredOn no debe ser null");
    assertFalse(
        occurredOn.isBefore(before),
        "occurredOn debe ser >= al instante anterior a la construcción");
    assertFalse(
        occurredOn.isAfter(after),
        "occurredOn debe ser <= al instante posterior a la construcción");
  }

  // ── article()

  @Test
  @DisplayName("article() debe devolver la misma instancia de ArticleModel recibida")
  void shouldReturnSameArticleInstance() {
    // Arrange
    final ArticleUpdatedDomainEvent event = new ArticleUpdatedDomainEvent(article);

    // Act
    final ArticleModel result = event.getArticle();

    // Assert
    assertSame(article, result);
  }

  // ── payload()

  @Test
  @DisplayName("payload() debe contener exactamente los once campos del artículo")
  void shouldReturnPayloadWithAllArticleFields() {
    // Arrange
    final ArticleUpdatedDomainEvent event = new ArticleUpdatedDomainEvent(article);

    // Act
    final Map<String, String> payload = event.payload();

    // Assert
    assertAll(
        "payload de ArticleUpdatedDomainEvent",
        () -> assertEquals(11, payload.size(), "tamaño del mapa"),
        () -> assertEquals(ID, payload.get("id"), "id"),
        () -> assertEquals(DESCRIPTION, payload.get("description"), "description"),
        () -> assertEquals(PROVIDER, payload.get("provider"), "provider"),
        () -> assertEquals(BRAND, payload.get("brand"), "brand"),
        () -> assertEquals(MODEL, payload.get("model"), "model"),
        () -> assertEquals("electronica", payload.get("category"), "category"),
        () -> assertEquals(STORE, payload.get("store"), "store"),
        () -> assertEquals("5", payload.get("quantity"), "quantity"),
        () -> assertEquals("2500000.50", payload.get("purchasePrice"), "purchasePrice"),
        () -> assertEquals("3200000.00", payload.get("salePrice"), "salePrice"),
        () -> assertEquals("19.00", payload.get("iva"), "iva"));
  }
}

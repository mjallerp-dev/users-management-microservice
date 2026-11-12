package com.jcaa.usersmanagement.domain.event;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.valueobject.ArticleId;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ArticleDeletedDomainEvent")
class ArticleDeletedDomainEventTest {

  private static final String ID = "article-002";

  // ── eventName

  @Test
  @DisplayName("eventName() debe retornar la constante 'article.deleted'")
  void shouldHaveEventNameArticleDeleted() {
    // Arrange
    final ArticleDeletedDomainEvent event = new ArticleDeletedDomainEvent(new ArticleId(ID));

    // Act
    final String result = event.getEventName();

    // Assert
    assertEquals("article.deleted", result);
  }

  // ── occurredOn

  @Test
  @DisplayName("occurredOn() no debe ser nulo y debe quedar acotado al instante de construcción")
  void shouldRecordOccurredOnAtCreationTime() {
    // Arrange
    final LocalDateTime before = LocalDateTime.now();
    final ArticleDeletedDomainEvent event = new ArticleDeletedDomainEvent(new ArticleId(ID));
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

  // ── articleId()

  @Test
  @DisplayName("articleId() debe devolver la misma instancia de ArticleId recibida")
  void shouldReturnSameArticleIdInstance() {
    // Arrange
    final ArticleId articleId = new ArticleId(ID);
    final ArticleDeletedDomainEvent event = new ArticleDeletedDomainEvent(articleId);

    // Act
    final ArticleId result = event.getArticleId();

    // Assert
    assertSame(articleId, result);
  }

  // ── payload()

  @Test
  @DisplayName("payload() debe contener únicamente la entrada 'id' con el valor del ArticleId")
  void shouldReturnPayloadWithOnlyArticleId() {
    // Arrange
    final ArticleDeletedDomainEvent event = new ArticleDeletedDomainEvent(new ArticleId(ID));

    // Act
    final Map<String, String> payload = event.payload();

    // Assert
    assertAll(
        "payload de ArticleDeletedDomainEvent",
        () -> assertEquals(1, payload.size(), "el mapa debe tener exactamente 1 entrada"),
        () -> assertEquals(ID, payload.get("id"), "id"));
  }
}

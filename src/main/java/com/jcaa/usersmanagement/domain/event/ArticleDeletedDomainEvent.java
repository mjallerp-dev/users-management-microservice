package com.jcaa.usersmanagement.domain.event;

import com.jcaa.usersmanagement.domain.valueobject.ArticleId;
import java.util.Map;
import lombok.Getter;

@Getter
public final class ArticleDeletedDomainEvent extends DomainEvent {

  private static final String EVENT_NAME = "article.deleted";

  private final ArticleId articleId;

  public ArticleDeletedDomainEvent(final ArticleId articleId) {
    super(EVENT_NAME);
    this.articleId = articleId;
  }

  @Override
  public Map<String, String> payload() {
    return Map.of("id", articleId.value());
  }
}

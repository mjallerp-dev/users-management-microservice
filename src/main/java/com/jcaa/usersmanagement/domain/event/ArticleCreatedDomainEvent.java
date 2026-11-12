package com.jcaa.usersmanagement.domain.event;

import com.jcaa.usersmanagement.domain.model.ArticleModel;
import java.util.Map;
import lombok.Getter;

@Getter
public final class ArticleCreatedDomainEvent extends DomainEvent {

  private static final String EVENT_NAME = "article.created";

  private final ArticleModel article;

  public ArticleCreatedDomainEvent(final ArticleModel article) {
    super(EVENT_NAME);
    this.article = article;
  }

  @Override
  public Map<String, String> payload() {
    return Map.ofEntries(
        Map.entry("id", article.getId().value()),
        Map.entry("description", article.getDescription().value()),
        Map.entry("provider", article.getProvider().value()),
        Map.entry("brand", article.getBrand().value()),
        Map.entry("model", article.getModel().value()),
        Map.entry("category", article.getCategory().value()),
        Map.entry("store", article.getStore().value()),
        Map.entry("quantity", article.getQuantity().toString()),
        Map.entry("purchasePrice", article.getPurchasePrice().toString()),
        Map.entry("salePrice", article.getSalePrice().toString()),
        Map.entry("iva", article.getIva().toString()));
  }
}

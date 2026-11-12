package com.jcaa.usersmanagement.infrastructure.adapter.persistence.mapper;

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
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.dto.ArticlePersistenceDto;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.entity.ArticleEntity;
import lombok.experimental.UtilityClass;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ArticlePersistenceMapper {

  public ArticlePersistenceDto fromModelToDto(final ArticleModel article) {
    return new ArticlePersistenceDto(
        article.getId().value(),
        article.getDescription().value(),
        article.getProvider().value(),
        article.getBrand().value(),
        article.getModel().value(),
        article.getCategory().value(),
        article.getStore().value(),
        article.getQuantity().value(),
        article.getPurchasePrice().value(),
        article.getSalePrice().value(),
        article.getIva().value(),
        null,
        null);
  }

  public ArticleEntity fromResultSetToEntity(final ResultSet resultSet) throws SQLException {
    return new ArticleEntity(
        resultSet.getString("id"),
        resultSet.getString("description"),
        resultSet.getString("provider"),
        resultSet.getString("brand"),
        resultSet.getString("model"),
        resultSet.getString("category"),
        resultSet.getString("store"),
        resultSet.getInt("quantity"),
        resultSet.getBigDecimal("purchase_price"),
        resultSet.getBigDecimal("sale_price"),
        resultSet.getBigDecimal("iva"),
        resultSet.getString("created_at"),
        resultSet.getString("updated_at"));
  }

  public ArticleModel fromEntityToModel(final ArticleEntity entity) {
    return new ArticleModel(
        new ArticleId(entity.id()),
        new ArticleDescription(entity.description()),
        new ArticleProvider(entity.provider()),
        new ArticleBrand(entity.brand()),
        new ArticleModelo(entity.model()),
        ArticleCategory.fromString(entity.category()),
        new ArticleStore(entity.store()),
        new ArticleQuantity(entity.quantity()),
        new ArticlePurchasePrice(entity.purchasePrice()),
        new ArticleSalePrice(entity.salePrice()),
        new ArticleIva(entity.iva()));
  }

  public ArticleModel fromResultSetToModel(final ResultSet resultSet) throws SQLException {
    return fromEntityToModel(fromResultSetToEntity(resultSet));
  }

  public List<ArticleModel> fromResultSetToModelList(final ResultSet resultSet)
      throws SQLException {
    final List<ArticleModel> articles = new ArrayList<>();
    while (resultSet.next()) {
      articles.add(fromResultSetToModel(resultSet));
    }
    return articles;
  }
}

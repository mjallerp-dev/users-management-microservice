package com.jcaa.usersmanagement.domain.model;

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
import lombok.Value;

@Value
public class ArticleModel {

  ArticleId id;
  ArticleDescription description;
  ArticleProvider provider;
  ArticleBrand brand;
  ArticleModelo model;
  ArticleCategory category;
  ArticleStore store;
  ArticleQuantity quantity;
  ArticlePurchasePrice purchasePrice;
  ArticleSalePrice salePrice;
  ArticleIva iva;

  public static ArticleModel create(
      final ArticleId id,
      final ArticleDescription description,
      final ArticleProvider provider,
      final ArticleBrand brand,
      final ArticleModelo model,
      final ArticleCategory category,
      final ArticleStore store,
      final ArticleQuantity quantity,
      final ArticlePurchasePrice purchasePrice,
      final ArticleSalePrice salePrice,
      final ArticleIva iva) {
    return new ArticleModel(
        id,
        description,
        provider,
        brand,
        model,
        category,
        store,
        quantity,
        purchasePrice,
        salePrice,
        iva);
  }
}

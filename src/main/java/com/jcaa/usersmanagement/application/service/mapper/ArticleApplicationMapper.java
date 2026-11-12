package com.jcaa.usersmanagement.application.service.mapper;

import com.jcaa.usersmanagement.application.service.dto.command.CreateArticleCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteArticleCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateArticleCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetArticleByIdQuery;
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
import lombok.experimental.UtilityClass;

@UtilityClass
public class ArticleApplicationMapper {

  public ArticleModel fromCreateCommandToModel(final CreateArticleCommand command) {
    return ArticleModel.create(
        new ArticleId(command.id()),
        new ArticleDescription(command.description()),
        new ArticleProvider(command.provider()),
        new ArticleBrand(command.brand()),
        new ArticleModelo(command.model()),
        ArticleCategory.fromString(command.category()),
        new ArticleStore(command.store()),
        new ArticleQuantity(command.quantity()),
        new ArticlePurchasePrice(command.purchasePrice()),
        new ArticleSalePrice(command.salePrice()),
        new ArticleIva(command.iva()));
  }

  public ArticleModel fromUpdateCommandToModel(final UpdateArticleCommand command) {
    return new ArticleModel(
        new ArticleId(command.id()),
        new ArticleDescription(command.description()),
        new ArticleProvider(command.provider()),
        new ArticleBrand(command.brand()),
        new ArticleModelo(command.model()),
        ArticleCategory.fromString(command.category()),
        new ArticleStore(command.store()),
        new ArticleQuantity(command.quantity()),
        new ArticlePurchasePrice(command.purchasePrice()),
        new ArticleSalePrice(command.salePrice()),
        new ArticleIva(command.iva()));
  }

  public ArticleId fromGetArticleByIdQueryToArticleId(final GetArticleByIdQuery query) {
    return new ArticleId(query.id());
  }

  public ArticleId fromDeleteCommandToArticleId(final DeleteArticleCommand command) {
    return new ArticleId(command.id());
  }
}

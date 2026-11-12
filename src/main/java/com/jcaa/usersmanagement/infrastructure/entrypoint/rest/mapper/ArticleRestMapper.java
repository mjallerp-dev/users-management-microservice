package com.jcaa.usersmanagement.infrastructure.entrypoint.rest.mapper;

import com.jcaa.usersmanagement.application.service.dto.command.CreateArticleCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteArticleCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateArticleCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetArticleByIdQuery;
import com.jcaa.usersmanagement.domain.model.ArticleModel;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.CreateArticleRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.UpdateArticleRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.response.ArticleRestResponse;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ArticleRestMapper {

  public CreateArticleCommand toCreateCommand(final CreateArticleRestRequest request) {
    return new CreateArticleCommand(
        request.id(),
        request.description(),
        request.provider(),
        request.brand(),
        request.model(),
        request.category(),
        request.store(),
        request.quantity(),
        request.purchasePrice(),
        request.salePrice(),
        request.iva());
  }

  public UpdateArticleCommand toUpdateCommand(
      final String id, final UpdateArticleRestRequest request) {
    return new UpdateArticleCommand(
        id,
        request.description(),
        request.provider(),
        request.brand(),
        request.model(),
        request.category(),
        request.store(),
        request.quantity(),
        request.purchasePrice(),
        request.salePrice(),
        request.iva());
  }

  public GetArticleByIdQuery toGetByIdQuery(final String id) {
    return new GetArticleByIdQuery(id);
  }

  public DeleteArticleCommand toDeleteCommand(final String id) {
    return new DeleteArticleCommand(id);
  }

  public ArticleRestResponse toResponse(final ArticleModel article) {
    return new ArticleRestResponse(
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
        article.getIva().value());
  }

  public List<ArticleRestResponse> toResponseList(final List<ArticleModel> articles) {
    return articles.stream().map(ArticleRestMapper::toResponse).toList();
  }
}

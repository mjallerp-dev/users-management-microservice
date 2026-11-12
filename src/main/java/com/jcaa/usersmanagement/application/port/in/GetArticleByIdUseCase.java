package com.jcaa.usersmanagement.application.port.in;

import com.jcaa.usersmanagement.application.service.dto.query.GetArticleByIdQuery;
import com.jcaa.usersmanagement.domain.model.ArticleModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface GetArticleByIdUseCase {
  ArticleModel execute(@NotNull @Valid GetArticleByIdQuery query);
}

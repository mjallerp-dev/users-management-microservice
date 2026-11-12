package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.model.ArticleModel;
import com.jcaa.usersmanagement.domain.valueobject.ArticleId;
import java.util.Optional;

public interface GetArticleByIdPort {
  Optional<ArticleModel> getById(ArticleId articleId);
}

package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.model.ArticleModel;

public interface UpdateArticlePort {
  ArticleModel update(ArticleModel article);
}

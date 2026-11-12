package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.model.ArticleModel;

public interface SaveArticlePort {
  ArticleModel save(ArticleModel article);
}

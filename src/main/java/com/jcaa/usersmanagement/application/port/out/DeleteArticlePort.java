package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.valueobject.ArticleId;

public interface DeleteArticlePort {
  void delete(ArticleId articleId);
}

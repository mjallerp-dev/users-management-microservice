package com.jcaa.usersmanagement.application.port.in;

import com.jcaa.usersmanagement.domain.model.ArticleModel;
import java.util.List;

public interface GetAllArticlesUseCase {
  List<ArticleModel> execute();
}

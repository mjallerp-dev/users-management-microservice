package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.model.ArticleModel;
import java.util.List;

public interface GetAllArticlesPort {
  List<ArticleModel> getAll();
}

package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.GetAllArticlesUseCase;
import com.jcaa.usersmanagement.application.port.out.GetAllArticlesPort;
import com.jcaa.usersmanagement.domain.model.ArticleModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllArticlesService implements GetAllArticlesUseCase {

  private final GetAllArticlesPort getAllArticlesPort;

  @Override
  public List<ArticleModel> execute() {
    return getAllArticlesPort.getAll();
  }
}

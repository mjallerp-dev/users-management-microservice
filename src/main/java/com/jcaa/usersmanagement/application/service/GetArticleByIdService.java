package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.GetArticleByIdUseCase;
import com.jcaa.usersmanagement.application.port.out.GetArticleByIdPort;
import com.jcaa.usersmanagement.application.service.dto.query.GetArticleByIdQuery;
import com.jcaa.usersmanagement.application.service.mapper.ArticleApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.ArticleNotFoundException;
import com.jcaa.usersmanagement.domain.model.ArticleModel;
import com.jcaa.usersmanagement.domain.valueobject.ArticleId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class GetArticleByIdService implements GetArticleByIdUseCase {

  private final GetArticleByIdPort getArticleByIdPort;
  private final Validator validator;

  @Override
  public ArticleModel execute(final GetArticleByIdQuery query) {
    validateQuery(query);

    final ArticleId articleId = ArticleApplicationMapper.fromGetArticleByIdQueryToArticleId(query);
    return getArticleByIdPort
        .getById(articleId)
        .orElseThrow(() -> ArticleNotFoundException.becauseIdWasNotFound(articleId.value()));
  }

  private void validateQuery(final GetArticleByIdQuery query) {
    final Set<ConstraintViolation<GetArticleByIdQuery>> violations = validator.validate(query);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}

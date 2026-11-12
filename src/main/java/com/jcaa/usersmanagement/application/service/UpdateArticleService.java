package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.UpdateArticleUseCase;
import com.jcaa.usersmanagement.application.port.out.GetArticleByIdPort;
import com.jcaa.usersmanagement.application.port.out.UpdateArticlePort;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateArticleCommand;
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
public class UpdateArticleService implements UpdateArticleUseCase {

  private final UpdateArticlePort updateArticlePort;
  private final GetArticleByIdPort getArticleByIdPort;
  private final Validator validator;

  @Override
  public ArticleModel execute(final UpdateArticleCommand command) {
    validateCommand(command);

    final ArticleModel articleToUpdate = ArticleApplicationMapper.fromUpdateCommandToModel(command);
    ensureArticleExists(articleToUpdate.getId());

    return updateArticlePort.update(articleToUpdate);
  }

  private void validateCommand(final UpdateArticleCommand command) {
    final Set<ConstraintViolation<UpdateArticleCommand>> violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  private void ensureArticleExists(final ArticleId articleId) {
    getArticleByIdPort
        .getById(articleId)
        .orElseThrow(() -> ArticleNotFoundException.becauseIdWasNotFound(articleId.value()));
  }
}

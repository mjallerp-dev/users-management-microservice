package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.DeleteArticleUseCase;
import com.jcaa.usersmanagement.application.port.out.DeleteArticlePort;
import com.jcaa.usersmanagement.application.port.out.GetArticleByIdPort;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteArticleCommand;
import com.jcaa.usersmanagement.application.service.mapper.ArticleApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.ArticleNotFoundException;
import com.jcaa.usersmanagement.domain.valueobject.ArticleId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class DeleteArticleService implements DeleteArticleUseCase {

  private final DeleteArticlePort deleteArticlePort;
  private final GetArticleByIdPort getArticleByIdPort;
  private final Validator validator;

  @Override
  public void execute(final DeleteArticleCommand command) {
    validateCommand(command);

    final ArticleId articleId = ArticleApplicationMapper.fromDeleteCommandToArticleId(command);
    ensureArticleExists(articleId);
    deleteArticlePort.delete(articleId);
  }

  private void validateCommand(final DeleteArticleCommand command) {
    final Set<ConstraintViolation<DeleteArticleCommand>> violations = validator.validate(command);
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

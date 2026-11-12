package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.CreateArticleUseCase;
import com.jcaa.usersmanagement.application.port.out.GetArticleByIdPort;
import com.jcaa.usersmanagement.application.port.out.SaveArticlePort;
import com.jcaa.usersmanagement.application.service.dto.command.CreateArticleCommand;
import com.jcaa.usersmanagement.application.service.mapper.ArticleApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.ArticleAlreadyExistsException;
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
public class CreateArticleService implements CreateArticleUseCase {

  private final SaveArticlePort saveArticlePort;
  private final GetArticleByIdPort getArticleByIdPort;
  private final Validator validator;

  @Override
  public ArticleModel execute(final CreateArticleCommand command) {
    validateCommand(command);

    final ArticleModel articleToSave = ArticleApplicationMapper.fromCreateCommandToModel(command);
    ensureIdIsNotTaken(articleToSave.getId());

    return saveArticlePort.save(articleToSave);
  }

  private void validateCommand(final CreateArticleCommand command) {
    final Set<ConstraintViolation<CreateArticleCommand>> violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  private void ensureIdIsNotTaken(final ArticleId articleId) {
    getArticleByIdPort
        .getById(articleId)
        .ifPresent(
            ignored -> {
              throw ArticleAlreadyExistsException.becauseIdAlreadyExists(articleId.value());
            });
  }
}

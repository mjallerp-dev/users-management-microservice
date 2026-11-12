package com.jcaa.usersmanagement.application.port.in;

import com.jcaa.usersmanagement.application.service.dto.command.CreateArticleCommand;
import com.jcaa.usersmanagement.domain.model.ArticleModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface CreateArticleUseCase {
  ArticleModel execute(@NotNull @Valid CreateArticleCommand command);
}

package com.jcaa.usersmanagement.application.port.in;

import com.jcaa.usersmanagement.application.service.dto.command.DeleteArticleCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface DeleteArticleUseCase {
  void execute(@NotNull @Valid DeleteArticleCommand command);
}

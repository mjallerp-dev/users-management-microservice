package com.jcaa.usersmanagement.infrastructure.entrypoint.rest.controller;

import com.jcaa.usersmanagement.application.port.in.CreateArticleUseCase;
import com.jcaa.usersmanagement.application.port.in.DeleteArticleUseCase;
import com.jcaa.usersmanagement.application.port.in.GetAllArticlesUseCase;
import com.jcaa.usersmanagement.application.port.in.GetArticleByIdUseCase;
import com.jcaa.usersmanagement.application.port.in.UpdateArticleUseCase;
import com.jcaa.usersmanagement.application.service.dto.command.CreateArticleCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteArticleCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateArticleCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetArticleByIdQuery;
import com.jcaa.usersmanagement.domain.model.ArticleModel;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.CreateArticleRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.UpdateArticleRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.response.ArticleRestResponse;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.mapper.ArticleRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleRestController implements ArticleRestControllerDocs {

  private final CreateArticleUseCase createArticleUseCase;
  private final UpdateArticleUseCase updateArticleUseCase;
  private final DeleteArticleUseCase deleteArticleUseCase;
  private final GetArticleByIdUseCase getArticleByIdUseCase;
  private final GetAllArticlesUseCase getAllArticlesUseCase;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ArticleRestResponse create(@Valid @RequestBody final CreateArticleRestRequest request) {
    final CreateArticleCommand command = ArticleRestMapper.toCreateCommand(request);
    final ArticleModel article = createArticleUseCase.execute(command);
    return ArticleRestMapper.toResponse(article);
  }

  @GetMapping
  public List<ArticleRestResponse> getAll() {
    return ArticleRestMapper.toResponseList(getAllArticlesUseCase.execute());
  }

  @GetMapping("/{id}")
  public ArticleRestResponse getById(@PathVariable final String id) {
    final GetArticleByIdQuery query = ArticleRestMapper.toGetByIdQuery(id);
    final ArticleModel article = getArticleByIdUseCase.execute(query);
    return ArticleRestMapper.toResponse(article);
  }

  @PutMapping("/{id}")
  public ArticleRestResponse update(
      @PathVariable final String id,
      @Valid @RequestBody final UpdateArticleRestRequest request) {
    final UpdateArticleCommand command = ArticleRestMapper.toUpdateCommand(id, request);
    final ArticleModel article = updateArticleUseCase.execute(command);
    return ArticleRestMapper.toResponse(article);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable final String id) {
    final DeleteArticleCommand command = ArticleRestMapper.toDeleteCommand(id);
    deleteArticleUseCase.execute(command);
  }
}

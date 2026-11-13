package com.jcaa.usersmanagement.infrastructure.entrypoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jcaa.usersmanagement.application.port.in.CreateArticleUseCase;
import com.jcaa.usersmanagement.application.port.in.DeleteArticleUseCase;
import com.jcaa.usersmanagement.application.port.in.GetAllArticlesUseCase;
import com.jcaa.usersmanagement.application.port.in.GetArticleByIdUseCase;
import com.jcaa.usersmanagement.application.port.in.UpdateArticleUseCase;
import com.jcaa.usersmanagement.application.service.dto.command.CreateArticleCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteArticleCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateArticleCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetArticleByIdQuery;
import com.jcaa.usersmanagement.domain.enums.ArticleCategory;
import com.jcaa.usersmanagement.domain.exception.ArticleAlreadyExistsException;
import com.jcaa.usersmanagement.domain.exception.ArticleNotFoundException;
import com.jcaa.usersmanagement.domain.exception.InvalidArticleCategoryException;
import com.jcaa.usersmanagement.domain.model.ArticleModel;
import com.jcaa.usersmanagement.domain.valueobject.ArticleBrand;
import com.jcaa.usersmanagement.domain.valueobject.ArticleDescription;
import com.jcaa.usersmanagement.domain.valueobject.ArticleId;
import com.jcaa.usersmanagement.domain.valueobject.ArticleIva;
import com.jcaa.usersmanagement.domain.valueobject.ArticleModelo;
import com.jcaa.usersmanagement.domain.valueobject.ArticleProvider;
import com.jcaa.usersmanagement.domain.valueobject.ArticlePurchasePrice;
import com.jcaa.usersmanagement.domain.valueobject.ArticleQuantity;
import com.jcaa.usersmanagement.domain.valueobject.ArticleSalePrice;
import com.jcaa.usersmanagement.domain.valueobject.ArticleStore;
import com.jcaa.usersmanagement.infrastructure.security.JwtAuthenticationFilter;
import com.jcaa.usersmanagement.infrastructure.security.JwtTokenService;
import com.jcaa.usersmanagement.infrastructure.security.RestAccessDeniedHandler;
import com.jcaa.usersmanagement.infrastructure.security.RestAuthenticationEntryPoint;
import com.jcaa.usersmanagement.infrastructure.security.SecurityConfig;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = ArticleRestController.class,
    properties = "spring.main.web-application-type=servlet")
@Import({
  SecurityConfig.class,
  JwtAuthenticationFilter.class,
  RestAuthenticationEntryPoint.class,
  RestAccessDeniedHandler.class
})
@DisplayName("ArticleRestController")
class ArticleRestControllerTest {

  private static final String ID = "a-001";
  private static final String VALID_BODY =
      """
      {
        "id": "%s",
        "description": "Portátil 14 pulgadas",
        "provider": "Proveedor Demo",
        "brand": "Lenovo",
        "model": "ThinkPad E14",
        "category": "electronica",
        "store": "Tienda Centro",
        "quantity": 5,
        "purchasePrice": 2500000.5,
        "salePrice": 3200000,
        "iva": 19
      }
      """;

  @Autowired private MockMvc mockMvc;

  @MockBean private CreateArticleUseCase createArticleUseCase;
  @MockBean private UpdateArticleUseCase updateArticleUseCase;
  @MockBean private DeleteArticleUseCase deleteArticleUseCase;
  @MockBean private GetArticleByIdUseCase getArticleByIdUseCase;
  @MockBean private GetAllArticlesUseCase getAllArticlesUseCase;
  @MockBean private JwtTokenService jwtTokenService;

  private static ArticleModel article(final String description) {
    return new ArticleModel(
        new ArticleId(ID),
        new ArticleDescription(description),
        new ArticleProvider("Proveedor Demo"),
        new ArticleBrand("Lenovo"),
        new ArticleModelo("ThinkPad E14"),
        ArticleCategory.ELECTRONICA,
        new ArticleStore("Tienda Centro"),
        new ArticleQuantity(5),
        new ArticlePurchasePrice(new BigDecimal("2500000.5")),
        new ArticleSalePrice(new BigDecimal("3200000")),
        new ArticleIva(new BigDecimal("19")));
  }

  @Test
  @WithMockUser(roles = "MEMBER")
  @DisplayName("GET /api/articles debe retornar todos los artículos")
  void shouldGetAllArticlesThroughSpringMvc() throws Exception {
    when(getAllArticlesUseCase.execute()).thenReturn(List.of(article("Portátil 14 pulgadas")));

    mockMvc
        .perform(get("/api/articles"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(ID))
        .andExpect(jsonPath("$[0].description").value("Portátil 14 pulgadas"))
        .andExpect(jsonPath("$[0].provider").value("Proveedor Demo"))
        .andExpect(jsonPath("$[0].brand").value("Lenovo"))
        .andExpect(jsonPath("$[0].model").value("ThinkPad E14"))
        .andExpect(jsonPath("$[0].category").value("electronica"))
        .andExpect(jsonPath("$[0].store").value("Tienda Centro"))
        .andExpect(jsonPath("$[0].quantity").value(5))
        .andExpect(jsonPath("$[0].purchasePrice").value(2500000.5))
        .andExpect(jsonPath("$[0].salePrice").value(3200000.0))
        .andExpect(jsonPath("$[0].iva").value(19.0));
  }

  @Test
  @WithMockUser(roles = "MEMBER")
  @DisplayName("GET /api/articles/{id} debe responder usando el caso de uso inyectado por Spring")
  void shouldGetArticleByIdThroughSpringMvc() throws Exception {
    // Arrange
    when(getArticleByIdUseCase.execute(new GetArticleByIdQuery(ID)))
        .thenReturn(article("Portátil 14 pulgadas"));

    // Act & Assert
    mockMvc
        .perform(get("/api/articles/{id}", ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(ID))
        .andExpect(jsonPath("$.category").value("electronica"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("POST /api/articles debe crear un artículo válido")
  void shouldCreateArticleThroughSpringMvc() throws Exception {
    when(createArticleUseCase.execute(
            new CreateArticleCommand(
                ID,
                "Portátil 14 pulgadas",
                "Proveedor Demo",
                "Lenovo",
                "ThinkPad E14",
                "electronica",
                "Tienda Centro",
                5,
                new BigDecimal("2500000.5"),
                new BigDecimal("3200000"),
                new BigDecimal("19"))))
        .thenReturn(article("Portátil 14 pulgadas"));

    mockMvc
        .perform(
            post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY.formatted(ID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(ID));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("PUT /api/articles/{id} debe actualizar un artículo")
  void shouldUpdateArticleThroughSpringMvc() throws Exception {
    final String request =
        """
        {
          "description": "Portátil 15 pulgadas",
          "provider": "Proveedor Demo",
          "brand": "Lenovo",
          "model": "ThinkPad E14",
          "category": "electronica",
          "store": "Tienda Centro",
          "quantity": 5,
          "purchasePrice": 2500000.5,
          "salePrice": 3200000,
          "iva": 19
        }
        """;
    when(updateArticleUseCase.execute(
            new UpdateArticleCommand(
                ID,
                "Portátil 15 pulgadas",
                "Proveedor Demo",
                "Lenovo",
                "ThinkPad E14",
                "electronica",
                "Tienda Centro",
                5,
                new BigDecimal("2500000.5"),
                new BigDecimal("3200000"),
                new BigDecimal("19"))))
        .thenReturn(article("Portátil 15 pulgadas"));

    mockMvc
        .perform(
            put("/api/articles/{id}", ID).contentType(MediaType.APPLICATION_JSON).content(request))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.description").value("Portátil 15 pulgadas"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("DELETE /api/articles/{id} debe eliminar un artículo")
  void shouldDeleteArticleThroughSpringMvc() throws Exception {
    mockMvc.perform(delete("/api/articles/{id}", ID)).andExpect(status().isNoContent());

    verify(deleteArticleUseCase).execute(new DeleteArticleCommand(ID));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("POST /api/articles debe rechazar una solicitud inválida antes del caso de uso")
  void shouldRejectInvalidCreateRequest() throws Exception {
    // Arrange
    final String invalidRequest =
        """
        {
          "id": "",
          "description": "TV",
          "provider": "AB",
          "brand": "LG",
          "model": "X1",
          "category": "",
          "store": "T1",
          "quantity": -1,
          "purchasePrice": 0,
          "salePrice": null,
          "iva": 150
        }
        """;

    // Act & Assert
    mockMvc
        .perform(
            post("/api/articles").contentType(MediaType.APPLICATION_JSON).content(invalidRequest))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
    verifyNoInteractions(createArticleUseCase);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("POST /api/articles debe responder 400 cuando el JSON está mal formado")
  void shouldReturnBadRequestWhenJsonIsMalformed() throws Exception {
    mockMvc
        .perform(
            post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": \"a-001\", \"description\": "))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.message").value("El cuerpo de la solicitud no es un JSON válido."));
    verifyNoInteractions(createArticleUseCase);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("POST /api/articles debe responder 400 cuando un número llega como texto")
  void shouldReturnBadRequestWhenNumberHasWrongType() throws Exception {
    final String request =
        VALID_BODY.formatted(ID).replace("\"quantity\": 5", "\"quantity\": \"cinco\"");

    mockMvc
        .perform(post("/api/articles").contentType(MediaType.APPLICATION_JSON).content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
    verifyNoInteractions(createArticleUseCase);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("POST /api/articles debe responder 400 cuando el cuerpo no es UTF-8 válido")
  void shouldReturnBadRequestWhenBodyIsNotValidUtf8() throws Exception {
    // "Portátil" codificado en ISO-8859-1: la 'á' es el byte 0xE1, inválido como UTF-8
    final byte[] latin1Body = VALID_BODY.formatted(ID).getBytes(StandardCharsets.ISO_8859_1);

    mockMvc
        .perform(
            post("/api/articles").contentType(MediaType.APPLICATION_JSON).content(latin1Body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
    verifyNoInteractions(createArticleUseCase);
  }

  @Test
  @DisplayName("GET /api/articles debe requerir autenticacion")
  void shouldRejectUnauthenticatedRequest() throws Exception {
    mockMvc
        .perform(get("/api/articles"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401));
    verifyNoInteractions(getAllArticlesUseCase);
  }

  @Test
  @DisplayName("POST /api/articles debe requerir autenticacion")
  void shouldRejectUnauthenticatedCreate() throws Exception {
    mockMvc
        .perform(
            post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY.formatted(ID)))
        .andExpect(status().isUnauthorized());
    verifyNoInteractions(createArticleUseCase);
  }

  @Test
  @WithMockUser(roles = "MEMBER")
  @DisplayName("GET /api/articles/{id} debe responder 404 cuando el artículo no existe")
  void shouldReturnNotFoundWhenArticleDoesNotExist() throws Exception {
    when(getArticleByIdUseCase.execute(any()))
        .thenThrow(ArticleNotFoundException.becauseIdWasNotFound("a-404"));

    mockMvc
        .perform(get("/api/articles/{id}", "a-404"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("The article with id 'a-404' was not found."));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("PUT /api/articles/{id} debe responder 404 cuando el artículo no existe")
  void shouldReturnNotFoundWhenUpdatingMissingArticle() throws Exception {
    final String request =
        """
        {
          "description": "Portátil 15 pulgadas",
          "provider": "Proveedor Demo",
          "brand": "Lenovo",
          "model": "ThinkPad E14",
          "category": "electronica",
          "store": "Tienda Centro",
          "quantity": 5,
          "purchasePrice": 2500000.5,
          "salePrice": 3200000,
          "iva": 19
        }
        """;
    when(updateArticleUseCase.execute(any()))
        .thenThrow(ArticleNotFoundException.becauseIdWasNotFound("a-404"));

    mockMvc
        .perform(
            put("/api/articles/{id}", "a-404")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("POST /api/articles debe responder 409 cuando el id ya existe")
  void shouldReturnConflictWhenArticleAlreadyExists() throws Exception {
    when(createArticleUseCase.execute(any()))
        .thenThrow(ArticleAlreadyExistsException.becauseIdAlreadyExists(ID));

    mockMvc
        .perform(
            post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY.formatted(ID)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.message").value("An article with id 'a-001' already exists."));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  @DisplayName("POST /api/articles debe responder 400 cuando la categoría no existe")
  void shouldReturnBadRequestWhenCategoryIsInvalid() throws Exception {
    when(createArticleUseCase.execute(any()))
        .thenThrow(InvalidArticleCategoryException.becauseValueIsInvalid("juguetes"));

    mockMvc
        .perform(
            post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY.formatted(ID)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("The article category 'juguetes' is not valid."));
  }

  @Test
  @WithMockUser(roles = "REVIEWER")
  @DisplayName("GET /api/articles debe permitir el acceso a un REVIEWER")
  void shouldAllowReviewerToGetArticles() throws Exception {
    when(getAllArticlesUseCase.execute()).thenReturn(List.of());

    mockMvc.perform(get("/api/articles")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "MEMBER")
  @DisplayName("POST /api/articles debe rechazar un MEMBER")
  void shouldRejectMemberCreatingArticle() throws Exception {
    mockMvc
        .perform(
            post("/api/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_BODY.formatted(ID)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403));
    verifyNoInteractions(createArticleUseCase);
  }

  @Test
  @WithMockUser(roles = "REVIEWER")
  @DisplayName("PUT /api/articles/{id} debe rechazar un REVIEWER")
  void shouldRejectReviewerUpdatingArticle() throws Exception {
    final String request =
        """
        {
          "description": "Portátil 15 pulgadas",
          "provider": "Proveedor Demo",
          "brand": "Lenovo",
          "model": "ThinkPad E14",
          "category": "electronica",
          "store": "Tienda Centro",
          "quantity": 5,
          "purchasePrice": 2500000.5,
          "salePrice": 3200000,
          "iva": 19
        }
        """;

    mockMvc
        .perform(
            put("/api/articles/{id}", ID).contentType(MediaType.APPLICATION_JSON).content(request))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403));
    verifyNoInteractions(updateArticleUseCase);
  }

  @Test
  @WithMockUser(roles = "MEMBER")
  @DisplayName("DELETE /api/articles/{id} debe rechazar un MEMBER")
  void shouldRejectMemberDeletingArticle() throws Exception {
    mockMvc
        .perform(delete("/api/articles/{id}", ID))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403));
    verifyNoInteractions(deleteArticleUseCase);
  }
}

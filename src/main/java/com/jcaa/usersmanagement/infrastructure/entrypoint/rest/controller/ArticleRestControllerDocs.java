package com.jcaa.usersmanagement.infrastructure.entrypoint.rest.controller;

import com.jcaa.usersmanagement.infrastructure.config.OpenApiConfig;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.CreateArticleRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.request.UpdateArticleRestRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.response.ApiErrorResponse;
import com.jcaa.usersmanagement.infrastructure.entrypoint.rest.dto.response.ArticleRestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Contrato OpenAPI / Swagger para {@link ArticleRestController}.
 *
 * <p>Toda la documentación de la API (anotaciones {@code @Operation}, {@code @ApiResponses}, etc.)
 * vive aquí para mantener el controlador limpio y enfocado únicamente en la lógica de despacho.
 */
@Tag(name = "Articles", description = "Gestión de artículos: crear, consultar, actualizar y eliminar.")
public interface ArticleRestControllerDocs {

  // ─────────────────────────────────────────────────────────────────────────────
  // POST /api/articles
  // ─────────────────────────────────────────────────────────────────────────────

  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
  @Operation(
      summary = "Crear artículo",
      description =
          "Registra un nuevo artículo. El ID debe ser único. "
              + "Categorías válidas: electronica, ropa, calzado, alimentos, bebidas, hogar, "
              + "belleza, deportes, papeleria, ferreteria, otros.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Artículo creado exitosamente.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ArticleRestResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Datos de entrada inválidos (campos vacíos, valores fuera de rango o categoría inexistente).",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Ya existe un artículo registrado con ese ID.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  ArticleRestResponse create(@Valid @RequestBody CreateArticleRestRequest request);

  // ─────────────────────────────────────────────────────────────────────────────
  // GET /api/articles
  // ─────────────────────────────────────────────────────────────────────────────

  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
  @Operation(
      summary = "Listar todos los artículos",
      description = "Retorna la lista completa de artículos ordenada alfabéticamente por descripción.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Lista de artículos obtenida exitosamente. Puede ser vacía.",
        content =
            @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ArticleRestResponse.class)))),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  @SuppressWarnings("unused") // Invocado por Spring MVC y leído por springdoc mediante reflexión.
  List<ArticleRestResponse> getAll();

  // ─────────────────────────────────────────────────────────────────────────────
  // GET /api/articles/{id}
  // ─────────────────────────────────────────────────────────────────────────────

  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
  @Operation(
      summary = "Obtener artículo por ID",
      description = "Retorna los datos de un artículo específico identificado por su ID único.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Artículo encontrado.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ArticleRestResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "No existe ningún artículo con el ID proporcionado.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  ArticleRestResponse getById(
      @Parameter(description = "ID único del artículo.", example = "art-001", required = true)
          @PathVariable
          String id);

  // ─────────────────────────────────────────────────────────────────────────────
  // PUT /api/articles/{id}
  // ─────────────────────────────────────────────────────────────────────────────

  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
  @Operation(
      summary = "Actualizar artículo",
      description = "Actualiza todos los datos de un artículo existente.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Artículo actualizado exitosamente.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ArticleRestResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Datos de entrada inválidos.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "No existe ningún artículo con el ID proporcionado.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  ArticleRestResponse update(
      @Parameter(description = "ID único del artículo a actualizar.", example = "art-001", required = true)
          @PathVariable
          String id,
      @Valid @RequestBody UpdateArticleRestRequest request);

  // ─────────────────────────────────────────────────────────────────────────────
  // DELETE /api/articles/{id}
  // ─────────────────────────────────────────────────────────────────────────────

  @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
  @Operation(
      summary = "Eliminar artículo",
      description = "Elimina permanentemente un artículo por su ID.")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Artículo eliminado exitosamente."),
    @ApiResponse(
        responseCode = "404",
        description = "No existe ningún artículo con el ID proporcionado.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Error interno del servidor.",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)))
  })
  void delete(
      @Parameter(description = "ID único del artículo a eliminar.", example = "art-001", required = true)
          @PathVariable
          String id);
}

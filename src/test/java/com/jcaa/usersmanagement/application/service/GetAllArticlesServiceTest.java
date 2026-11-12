package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetAllArticlesPort;
import com.jcaa.usersmanagement.domain.model.ArticleModel;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para GetAllArticlesService.
 *
 * <p>Cubre: lista con artículos y lista vacía.
 */
@DisplayName("GetAllArticlesService")
@ExtendWith(MockitoExtension.class)
class GetAllArticlesServiceTest {

  @Mock private GetAllArticlesPort getAllArticlesPort;
  @Mock private ArticleModel article;

  private GetAllArticlesService service;

  @BeforeEach
  void setUp() {
    service = new GetAllArticlesService(getAllArticlesPort);
  }

  // ── lista con artículos

  @Test
  @DisplayName("execute() retorna la lista de artículos del puerto")
  void shouldReturnArticlesFromPort() {
    // Arrange
    when(getAllArticlesPort.getAll()).thenReturn(List.of(article));

    // Act
    final List<ArticleModel> result = service.execute();

    // Assert
    assertAll(
        "getAll con un artículo",
        () -> assertEquals(1, result.size(), "debe retornar exactamente un artículo"),
        () -> assertSame(article, result.get(0), "debe ser el mismo objeto del puerto"));
  }

  // ── lista vacía

  @Test
  @DisplayName("execute() retorna lista vacía cuando no hay artículos")
  void shouldReturnEmptyListWhenNoArticles() {
    // Arrange
    when(getAllArticlesPort.getAll()).thenReturn(List.of());

    // Act
    final List<ArticleModel> result = service.execute();

    // Assert
    assertTrue(result.isEmpty(), "debe retornar lista vacía");
  }
}

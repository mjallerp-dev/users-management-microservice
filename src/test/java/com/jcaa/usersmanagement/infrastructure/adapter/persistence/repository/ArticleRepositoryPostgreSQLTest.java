package com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.domain.enums.ArticleCategory;
import com.jcaa.usersmanagement.domain.exception.ArticleNotFoundException;
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
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.PersistenceException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for ArticleRepositoryPostgreSQL.
 *
 * <p>Covers all public methods with their branches: save() — happy path with bound parameters,
 * INSERT failure, article not found after insert (orElseThrow); update() — happy path with bound
 * parameters, UPDATE failure; getById() — found, not found, SQLException from prepareStatement,
 * executeQuery and close(); getAll() — happy path, SQLException; delete() — happy path,
 * SQLException.
 */
@DisplayName("ArticleRepositoryPostgreSQL")
@ExtendWith(MockitoExtension.class)
class ArticleRepositoryPostgreSQLTest {

  private static final String ID = "a-001";
  private static final String DESCRIPTION = "Portátil 14 pulgadas";
  private static final String PROVIDER = "Proveedor Demo";
  private static final String BRAND = "Lenovo";
  private static final String MODEL = "ThinkPad E14";
  private static final String CATEGORY = "electronica";
  private static final String STORE = "Tienda Centro";
  private static final int QUANTITY = 5;
  private static final BigDecimal PURCHASE_PRICE = new BigDecimal("2500000.50");
  private static final BigDecimal SALE_PRICE = new BigDecimal("3200000.00");
  private static final BigDecimal IVA = new BigDecimal("19.00");
  private static final String CREATED_AT = "2024-01-01";
  private static final String UPDATED_AT = "2024-01-02";

  @Mock private DataSource dataSource;
  @Mock private Connection connection;
  @Mock private PreparedStatement statement;
  @Mock private ResultSet resultSet;

  private ArticleRepositoryPostgreSQL repository;
  private ArticleModel articleModel;
  private ArticleId articleId;

  @BeforeEach
  void setUp() {
    repository = new ArticleRepositoryPostgreSQL(dataSource);
    articleId = new ArticleId(ID);
    articleModel =
        new ArticleModel(
            articleId,
            new ArticleDescription(DESCRIPTION),
            new ArticleProvider(PROVIDER),
            new ArticleBrand(BRAND),
            new ArticleModelo(MODEL),
            ArticleCategory.ELECTRONICA,
            new ArticleStore(STORE),
            new ArticleQuantity(QUANTITY),
            new ArticlePurchasePrice(PURCHASE_PRICE),
            new ArticleSalePrice(SALE_PRICE),
            new ArticleIva(IVA));
  }

  // Helper: wire dataSource → connection → statement → resultSet
  private void configureStatementAndResultSet() throws SQLException {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenReturn(resultSet);
  }

  // Helper: configure resultSet to return one full article row
  private void configureResultSetRow() throws SQLException {
    when(resultSet.getString("id")).thenReturn(ID);
    when(resultSet.getString("description")).thenReturn(DESCRIPTION);
    when(resultSet.getString("provider")).thenReturn(PROVIDER);
    when(resultSet.getString("brand")).thenReturn(BRAND);
    when(resultSet.getString("model")).thenReturn(MODEL);
    when(resultSet.getString("category")).thenReturn(CATEGORY);
    when(resultSet.getString("store")).thenReturn(STORE);
    when(resultSet.getInt("quantity")).thenReturn(QUANTITY);
    when(resultSet.getBigDecimal("purchase_price")).thenReturn(PURCHASE_PRICE);
    when(resultSet.getBigDecimal("sale_price")).thenReturn(SALE_PRICE);
    when(resultSet.getBigDecimal("iva")).thenReturn(IVA);
    when(resultSet.getString("created_at")).thenReturn(CREATED_AT);
    when(resultSet.getString("updated_at")).thenReturn(UPDATED_AT);
  }

  // ── save() — happy path

  @Test
  @DisplayName("save() binds all INSERT parameters and returns the persisted article by id")
  void shouldSaveArticleAndReturnById() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true);
    configureResultSetRow();

    // Act
    final ArticleModel result = repository.save(articleModel);

    // Assert
    assertEquals(articleModel, result, "must return the article read back after INSERT");
    // id is bound twice: in the INSERT and in the SELECT that reads the article back
    verify(statement, times(2)).setString(1, ID);
    verify(statement).setString(6, CATEGORY);
    verify(statement).setInt(8, QUANTITY);
    verify(statement).setBigDecimal(9, PURCHASE_PRICE);
    verify(statement).setBigDecimal(10, SALE_PRICE);
    verify(statement).setBigDecimal(11, IVA);
    verify(statement).executeUpdate();
  }

  // ── save() — INSERT fails → PersistenceException

  @Test
  @DisplayName("save() throws PersistenceException when INSERT raises SQLException")
  void shouldThrowPersistenceExceptionWhenInsertFails() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeUpdate()).thenThrow(new SQLException("Insert failed"));

    // Act
    final PersistenceException exception =
        assertThrows(PersistenceException.class, () -> repository.save(articleModel));

    // Assert
    assertEquals("Failed to save article with ID: 'a-001'.", exception.getMessage());
  }

  // ── save() → findByIdOrFail — article not found after insert → ArticleNotFoundException

  @Test
  @DisplayName("save() throws ArticleNotFoundException when the saved article cannot be found")
  void shouldThrowArticleNotFoundExceptionWhenArticleNotFoundAfterSave() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(false);

    // Act + Assert
    assertThrows(
        ArticleNotFoundException.class,
        () -> repository.save(articleModel),
        "must throw ArticleNotFoundException when SELECT returns no rows after INSERT");
  }

  // ── update() — happy path

  @Test
  @DisplayName("update() binds all UPDATE parameters and returns the refreshed article by id")
  void shouldUpdateArticleAndReturnById() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true);
    configureResultSetRow();

    // Act
    final ArticleModel result = repository.update(articleModel);

    // Assert
    assertEquals(articleModel, result, "must return the article read back after UPDATE");
    verify(statement).setString(5, CATEGORY);
    verify(statement).setInt(7, QUANTITY);
    verify(statement).setBigDecimal(10, IVA);
    verify(statement).setString(11, ID);
    verify(statement).executeUpdate();
  }

  // ── update() — UPDATE fails → PersistenceException

  @Test
  @DisplayName("update() throws PersistenceException when UPDATE raises SQLException")
  void shouldThrowPersistenceExceptionWhenUpdateFails() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeUpdate()).thenThrow(new SQLException("Update failed"));

    // Act
    final PersistenceException exception =
        assertThrows(PersistenceException.class, () -> repository.update(articleModel));

    // Assert
    assertEquals("Failed to update article with ID: 'a-001'.", exception.getMessage());
  }

  // ── getById() — row found → Optional.of(article)

  @Test
  @DisplayName("getById() returns Optional.of(article) when a matching row exists")
  void shouldReturnArticleWhenFound() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true);
    configureResultSetRow();

    // Act
    final Optional<ArticleModel> result = repository.getById(articleId);

    // Assert
    assertEquals(Optional.of(articleModel), result, "must return the matching article");
    verify(statement).setString(1, ID);
  }

  // ── getById() — no row → Optional.empty()

  @Test
  @DisplayName("getById() returns Optional.empty() when no matching row exists")
  void shouldReturnEmptyWhenNotFound() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(false);

    // Act
    final Optional<ArticleModel> result = repository.getById(articleId);

    // Assert
    assertTrue(result.isEmpty(), "must return Optional.empty() when no row matches the id");
  }

  // ── getById() — SQLException → PersistenceException (from prepareStatement)

  @Test
  @DisplayName("getById() throws PersistenceException when prepareStatement raises SQLException")
  void shouldThrowPersistenceExceptionOnGetByIdFailure() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Query failed"));

    // Act
    final PersistenceException exception =
        assertThrows(PersistenceException.class, () -> repository.getById(articleId));

    // Assert
    assertEquals("Failed to find article with ID: 'a-001'.", exception.getMessage());
  }

  // ── getById() — SQLException → PersistenceException (from executeQuery, inside try body)

  @Test
  @DisplayName("getById() throws PersistenceException when executeQuery raises SQLException")
  void shouldThrowPersistenceExceptionWhenGetByIdExecuteQueryFails() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenThrow(new SQLException("Execute query failed"));

    // Act + Assert
    assertThrows(
        PersistenceException.class,
        () -> repository.getById(articleId),
        "must throw PersistenceException when executeQuery raises SQLException inside the try block");
  }

  // ── getById() — SQLException → PersistenceException (from statement.close() after normal exit)

  @Test
  @DisplayName(
      "getById() throws PersistenceException when PreparedStatement.close() raises SQLException")
  void shouldThrowPersistenceExceptionWhenGetByIdStatementCloseFails() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(false);
    doThrow(new SQLException("Close failed")).when(statement).close();

    // Act + Assert
    assertThrows(
        PersistenceException.class,
        () -> repository.getById(articleId),
        "must throw PersistenceException when PreparedStatement.close() raises SQLException after normal body exit");
  }

  // ── getAll() — happy path

  @Test
  @DisplayName("getAll() returns one model per row in the result set")
  void shouldReturnAllArticles() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true, false);
    configureResultSetRow();

    // Act
    final List<ArticleModel> result = repository.getAll();

    // Assert
    assertEquals(List.of(articleModel), result, "must return one article per row");
  }

  // ── getAll() — SQLException → PersistenceException

  @Test
  @DisplayName("getAll() throws PersistenceException when the query raises SQLException")
  void shouldThrowPersistenceExceptionOnGetAllFailure() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Query failed"));

    // Act
    final PersistenceException exception =
        assertThrows(PersistenceException.class, () -> repository.getAll());

    // Assert
    assertEquals("Failed to retrieve all articles.", exception.getMessage());
  }

  // ── delete() — happy path

  @Test
  @DisplayName("delete() binds the id and executes DELETE")
  void shouldDeleteArticle() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(statement);

    // Act
    repository.delete(articleId);

    // Assert
    verify(statement).setString(1, ID);
    verify(statement).executeUpdate();
  }

  // ── delete() — SQLException → PersistenceException

  @Test
  @DisplayName("delete() throws PersistenceException when DELETE raises SQLException")
  void shouldThrowPersistenceExceptionWhenDeleteFails() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Delete failed"));

    // Act
    final PersistenceException exception =
        assertThrows(PersistenceException.class, () -> repository.delete(articleId));

    // Assert
    assertEquals("Failed to delete article with ID: 'a-001'.", exception.getMessage());
  }
}

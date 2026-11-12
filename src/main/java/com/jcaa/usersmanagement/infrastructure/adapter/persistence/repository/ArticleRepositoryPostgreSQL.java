package com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository;

import com.jcaa.usersmanagement.application.port.out.DeleteArticlePort;
import com.jcaa.usersmanagement.application.port.out.GetAllArticlesPort;
import com.jcaa.usersmanagement.application.port.out.GetArticleByIdPort;
import com.jcaa.usersmanagement.application.port.out.SaveArticlePort;
import com.jcaa.usersmanagement.application.port.out.UpdateArticlePort;
import com.jcaa.usersmanagement.domain.exception.ArticleNotFoundException;
import com.jcaa.usersmanagement.domain.model.ArticleModel;
import com.jcaa.usersmanagement.domain.valueobject.ArticleId;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.dto.ArticlePersistenceDto;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.PersistenceException;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.mapper.ArticlePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryPostgreSQL
    implements SaveArticlePort,
        UpdateArticlePort,
        GetArticleByIdPort,
        GetAllArticlesPort,
        DeleteArticlePort {

  private static final String SQL_INSERT =
      "INSERT INTO articles "
      + "(id, description, provider, brand, model, category, store, quantity, "
      + "purchase_price, sale_price, iva, created_at, updated_at) "
      + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

  private static final String SQL_UPDATE =
      "UPDATE articles SET description = ?, provider = ?, brand = ?, model = ?, category = ?, "
      + "store = ?, quantity = ?, purchase_price = ?, sale_price = ?, iva = ?, updated_at = NOW() "
      + "WHERE id = ?";

  private static final String SQL_SELECT_BY_ID =
      "SELECT id, description, provider, brand, model, category, store, quantity, "
      + "purchase_price, sale_price, iva, created_at, updated_at "
      + "FROM articles "
      + "WHERE id = ? LIMIT 1";

  private static final String SQL_SELECT_ALL =
      "SELECT id, description, provider, brand, model, category, store, quantity, "
      + "purchase_price, sale_price, iva, created_at, updated_at "
      + "FROM articles "
      + "ORDER BY description ASC";

  private static final String SQL_DELETE =
      "DELETE FROM articles "
      + "WHERE id = ?";

  private final DataSource dataSource;

  @Override
  public ArticleModel save(final ArticleModel article) {
    final ArticlePersistenceDto dto = ArticlePersistenceMapper.fromModelToDto(article);
    executeSave(dto);
    return findByIdOrFail(article.getId());
  }

  @Override
  public ArticleModel update(final ArticleModel article) {
    final ArticlePersistenceDto dto = ArticlePersistenceMapper.fromModelToDto(article);
    executeUpdate(dto);
    return findByIdOrFail(article.getId());
  }

  @Override
  public Optional<ArticleModel> getById(final ArticleId articleId) {
    try (final Connection connection = dataSource.getConnection();
         final PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {
      statement.setString(1, articleId.value());
      final ResultSet resultSet = statement.executeQuery();
      if (!resultSet.next()) {
        return Optional.empty();
      }
      return Optional.of(ArticlePersistenceMapper.fromResultSetToModel(resultSet));
    } catch (final SQLException exception) {
      throw PersistenceException.becauseArticleFindByIdFailed(articleId.value(), exception);
    }
  }

  @Override
  public List<ArticleModel> getAll() {
    try (final Connection connection = dataSource.getConnection();
         final PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL)) {
      final ResultSet resultSet = statement.executeQuery();
      return ArticlePersistenceMapper.fromResultSetToModelList(resultSet);
    } catch (final SQLException exception) {
      throw PersistenceException.becauseArticleFindAllFailed(exception);
    }
  }

  @Override
  public void delete(final ArticleId articleId) {
    try (final Connection connection = dataSource.getConnection();
         final PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
      statement.setString(1, articleId.value());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw PersistenceException.becauseArticleDeleteFailed(articleId.value(), exception);
    }
  }

  private void executeSave(final ArticlePersistenceDto dto) {
    try (final Connection connection = dataSource.getConnection();
         final PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
      statement.setString(1, dto.id());
      statement.setString(2, dto.description());
      statement.setString(3, dto.provider());
      statement.setString(4, dto.brand());
      statement.setString(5, dto.model());
      statement.setString(6, dto.category());
      statement.setString(7, dto.store());
      statement.setInt(8, dto.quantity());
      statement.setBigDecimal(9, dto.purchasePrice());
      statement.setBigDecimal(10, dto.salePrice());
      statement.setBigDecimal(11, dto.iva());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw PersistenceException.becauseArticleSaveFailed(dto.id(), exception);
    }
  }

  private void executeUpdate(final ArticlePersistenceDto dto) {
    try (final Connection connection = dataSource.getConnection();
         final PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
      statement.setString(1, dto.description());
      statement.setString(2, dto.provider());
      statement.setString(3, dto.brand());
      statement.setString(4, dto.model());
      statement.setString(5, dto.category());
      statement.setString(6, dto.store());
      statement.setInt(7, dto.quantity());
      statement.setBigDecimal(8, dto.purchasePrice());
      statement.setBigDecimal(9, dto.salePrice());
      statement.setBigDecimal(10, dto.iva());
      statement.setString(11, dto.id());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw PersistenceException.becauseArticleUpdateFailed(dto.id(), exception);
    }
  }

  private ArticleModel findByIdOrFail(final ArticleId articleId) {
    return getById(articleId)
        .orElseThrow(() -> ArticleNotFoundException.becauseIdWasNotFound(articleId.value()));
  }
}

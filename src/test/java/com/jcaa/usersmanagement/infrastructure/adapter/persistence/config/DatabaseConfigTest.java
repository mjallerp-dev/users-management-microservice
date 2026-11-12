package com.jcaa.usersmanagement.infrastructure.adapter.persistence.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DatabaseConfigTest {

  private static final String HOST = "my-server.postgres.database.azure.com";
  private static final int PORT = 5432;
  private static final String DATABASE = "pgarticlesdb";
  private static final String USERNAME = "pgadmin";
  private static final String PASSWORD = "secret";
  private static final String SSL_MODE = "require";

  @Test
  void shouldBuildPostgreSqlJdbcUrlWithConfiguredSslMode() {
    // Arrange
    final DatabaseConfig config =
        new DatabaseConfig(HOST, PORT, DATABASE, USERNAME, PASSWORD, SSL_MODE);

    // Act
    final String jdbcUrl = config.buildJdbcUrl();

    // Assert
    assertThat(jdbcUrl)
        .isEqualTo(
            "jdbc:postgresql://my-server.postgres.database.azure.com:5432/pgarticlesdb"
                + "?sslmode=require");
  }
}

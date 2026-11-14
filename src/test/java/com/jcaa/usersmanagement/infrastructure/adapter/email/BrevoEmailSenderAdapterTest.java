package com.jcaa.usersmanagement.infrastructure.adapter.email;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withCreatedEntity;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.jcaa.usersmanagement.domain.exception.EmailSenderException;
import com.jcaa.usersmanagement.domain.model.EmailDestinationModel;
import java.io.IOException;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

/**
 * Tests para BrevoEmailSenderAdapter.
 *
 * <p>La API de Brevo se simula con MockRestServiceServer: no se envía ningún correo real.
 */
@DisplayName("BrevoEmailSenderAdapter")
class BrevoEmailSenderAdapterTest {

  private static final String BASE_URL = "https://api.brevo.test/v3";
  private static final String API_KEY = "test-api-key";
  private static final BrevoConfig CONFIG =
      new BrevoConfig(API_KEY, BASE_URL, "remitente@test.local", "Gestion de Usuarios");

  private MockRestServiceServer server;
  private BrevoEmailSenderAdapter adapter;
  private EmailDestinationModel destination;

  @BeforeEach
  void setUp() {
    final RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
    server = MockRestServiceServer.bindTo(builder).build();
    adapter = new BrevoEmailSenderAdapter(builder.build(), CONFIG);
    destination =
        new EmailDestinationModel(
            "ada@example.com", "Ada Lovelace", "Tu cuenta ha sido creada", "<p>Hola Ada</p>");
  }

  @Test
  @DisplayName("send() envía a Brevo la petición con la API key, el remitente y el destinatario")
  void shouldSendEmailThroughBrevoApi() {
    // Arrange
    server
        .expect(requestTo(BASE_URL + "/smtp/email"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(header("api-key", API_KEY))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.sender.email").value("remitente@test.local"))
        .andExpect(jsonPath("$.sender.name").value("Gestion de Usuarios"))
        .andExpect(jsonPath("$.to[0].email").value("ada@example.com"))
        .andExpect(jsonPath("$.to[0].name").value("Ada Lovelace"))
        .andExpect(jsonPath("$.subject").value("Tu cuenta ha sido creada"))
        .andExpect(jsonPath("$.htmlContent").value("<p>Hola Ada</p>"))
        .andRespond(withCreatedEntity(URI.create(BASE_URL + "/smtp/email")));

    // Act & Assert
    assertDoesNotThrow(() -> adapter.send(destination));
    server.verify();
  }

  @Test
  @DisplayName("send() lanza EmailSenderException cuando Brevo rechaza la petición")
  void shouldThrowWhenBrevoRejectsRequest() {
    // Arrange
    server
        .expect(requestTo(BASE_URL + "/smtp/email"))
        .andRespond(
            withStatus(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"code\":\"unauthorized\",\"message\":\"Key not found\"}"));

    // Act & Assert
    assertThrows(EmailSenderException.class, () -> adapter.send(destination));
    server.verify();
  }

  @Test
  @DisplayName("send() lanza EmailSenderException cuando no se puede conectar con Brevo")
  void shouldThrowWhenBrevoIsUnreachable() {
    // Arrange
    server
        .expect(requestTo(BASE_URL + "/smtp/email"))
        .andRespond(withException(new IOException("Connection refused")));

    // Act & Assert
    assertThrows(EmailSenderException.class, () -> adapter.send(destination));
    server.verify();
  }
}

package com.jcaa.usersmanagement.infrastructure.adapter.email;

import com.jcaa.usersmanagement.application.port.out.EmailSenderPort;
import com.jcaa.usersmanagement.domain.exception.EmailSenderException;
import com.jcaa.usersmanagement.domain.model.EmailDestinationModel;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Component
@ConditionalOnProperty(name = "email.provider", havingValue = "brevo")
public class BrevoEmailSenderAdapter implements EmailSenderPort {

  private static final String SEND_EMAIL_PATH = "/smtp/email";
  private static final String API_KEY_HEADER = "api-key";
  private static final String LOG_SENT = "[BrevoEmailSenderAdapter] correo enviado exitosamente.";
  private static final String LOG_REJECTED =
      "[BrevoEmailSenderAdapter] Brevo rechazó el correo. HTTP {}: {}";

  private final RestClient restClient;
  private final BrevoConfig config;

  public BrevoEmailSenderAdapter(
      @Qualifier("brevoRestClient") final RestClient restClient, final BrevoConfig config) {
    this.restClient = restClient;
    this.config = config;
  }

  @Override
  public void send(final EmailDestinationModel destination) {
    try {
      restClient
          .post()
          .uri(SEND_EMAIL_PATH)
          .header(API_KEY_HEADER, config.apiKey())
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .body(buildRequest(destination))
          .retrieve()
          .toBodilessEntity();
      log.info(LOG_SENT);
    } catch (final RestClientResponseException exception) {
      log.warn(
          LOG_REJECTED, exception.getStatusCode().value(), exception.getResponseBodyAsString());
      throw EmailSenderException.becauseSendFailed(exception);
    } catch (final RestClientException exception) {
      throw EmailSenderException.becauseSendFailed(exception);
    }
  }

  private BrevoEmailRequest buildRequest(final EmailDestinationModel destination) {
    return new BrevoEmailRequest(
        new BrevoContact(config.fromAddress(), config.fromName()),
        List.of(
            new BrevoContact(destination.getDestinationEmail(), destination.getDestinationName())),
        destination.getSubject(),
        destination.getBody());
  }

  record BrevoContact(String email, String name) {}

  record BrevoEmailRequest(
      BrevoContact sender, List<BrevoContact> to, String subject, String htmlContent) {}
}

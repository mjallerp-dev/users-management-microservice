package com.jcaa.usersmanagement.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.jcaa.usersmanagement.application.port.out.EmailSenderPort;
import com.jcaa.usersmanagement.infrastructure.adapter.email.BrevoEmailSenderAdapter;
import com.jcaa.usersmanagement.infrastructure.adapter.email.JavaMailEmailSenderAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Verifica que EMAIL_PROVIDER (propiedad email.provider) seleccione un único adaptador de correo.
 */
@DisplayName("Selección del proveedor de correo")
class EmailProviderSelectionTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class))
          .withUserConfiguration(
              SmtpSpringConfig.class,
              BrevoSpringConfig.class,
              JavaMailEmailSenderAdapter.class,
              BrevoEmailSenderAdapter.class)
          .withPropertyValues(
              "smtp.host=smtp.test.local",
              "smtp.port=587",
              "smtp.username=test@test.local",
              "smtp.password=test",
              "smtp.from.address=test@test.local",
              "smtp.from.name=Test",
              "brevo.base-url=https://api.brevo.test/v3");

  @Test
  @DisplayName("Sin email.provider se usa SMTP")
  void shouldUseSmtpByDefault() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(EmailSenderPort.class);
          assertThat(context).hasSingleBean(JavaMailEmailSenderAdapter.class);
          assertThat(context).doesNotHaveBean(BrevoEmailSenderAdapter.class);
        });
  }

  @Test
  @DisplayName("Con email.provider=brevo se usa la API de Brevo")
  void shouldUseBrevoWhenConfigured() {
    contextRunner
        .withPropertyValues("email.provider=brevo", "brevo.api-key=test-key")
        .run(
            context -> {
              assertThat(context).hasSingleBean(EmailSenderPort.class);
              assertThat(context).hasSingleBean(BrevoEmailSenderAdapter.class);
              assertThat(context).doesNotHaveBean(JavaMailEmailSenderAdapter.class);
            });
  }

  @Test
  @DisplayName("Con email.provider=brevo y sin API key la aplicación no arranca")
  void shouldFailWhenBrevoApiKeyIsMissing() {
    contextRunner
        .withPropertyValues("email.provider=brevo", "brevo.api-key=")
        .run(
            context ->
                assertThat(context)
                    .hasFailed()
                    .getFailure()
                    .rootCause()
                    .hasMessageContaining("BREVO_API_KEY"));
  }
}

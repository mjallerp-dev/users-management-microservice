package com.jcaa.usersmanagement.infrastructure.config;

import com.jcaa.usersmanagement.infrastructure.adapter.email.BrevoConfig;
import java.net.http.HttpClient;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "email.provider", havingValue = "brevo")
public class BrevoSpringConfig {

  private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
  private static final Duration READ_TIMEOUT = Duration.ofSeconds(20);
  private static final String MISSING_API_KEY =
      "EMAIL_PROVIDER=brevo requiere la variable de entorno BREVO_API_KEY.";
  private static final String LOG_INIT = "[BrevoSpringConfig] envío de correo con la API de Brevo.";

  @Value("${brevo.api-key}")
  private String apiKey;

  @Value("${brevo.base-url}")
  private String baseUrl;

  @Value("${smtp.from.address}")
  private String fromAddress;

  @Value("${smtp.from.name}")
  private String fromName;

  @Bean
  public BrevoConfig brevoConfig() {
    if (apiKey == null || apiKey.isBlank()) {
      throw new IllegalStateException(MISSING_API_KEY);
    }
    log.info(LOG_INIT);
    return new BrevoConfig(apiKey, baseUrl, fromAddress, fromName);
  }

  @Bean
  public RestClient brevoRestClient(final RestClient.Builder builder, final BrevoConfig config) {
    final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build();
    final JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
    requestFactory.setReadTimeout(READ_TIMEOUT);
    return builder.baseUrl(config.baseUrl()).requestFactory(requestFactory).build();
  }
}

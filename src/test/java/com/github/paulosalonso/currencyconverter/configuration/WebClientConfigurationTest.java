package com.github.paulosalonso.currencyconverter.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class WebClientConfigurationTest {

  private WebClientConfiguration webClientConfiguration = new WebClientConfiguration();

  @Test
  void givenABaseUrlWhenGetExchangeRateApiWebClientThenReturnInstanceWithThatBaseUrl() {
    final var baseUrl = "base-url";
    final var webClient = webClientConfiguration.exchangeRateApiWebClient(baseUrl);
    final var webClientBuilder = ReflectionTestUtils.getField(webClient, "builder");
    assertThat(ReflectionTestUtils.getField(webClientBuilder, "baseUrl")).isEqualTo(baseUrl);
  }

}

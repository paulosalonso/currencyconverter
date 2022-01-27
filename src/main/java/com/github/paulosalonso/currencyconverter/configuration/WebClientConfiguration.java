package com.github.paulosalonso.currencyconverter.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

  @Bean
  public WebClient exchangeRateApiWebClient(
      @Value("${spring.feign.exchange-rate-api-base-url}") String exchangeRateApiBaseUrl) {

    return WebClient.create(exchangeRateApiBaseUrl);
  }
}

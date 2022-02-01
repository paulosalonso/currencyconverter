package com.github.paulosalonso.currencyconverter.repository.http;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.github.paulosalonso.currencyconverter.repository.http.dto.ExchangeRateErrorDto;
import com.github.paulosalonso.currencyconverter.repository.http.dto.ExchangeRateResponseDto;
import java.util.Map;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ExchangeRateApiClient {

  private static final Map<String, Supplier<? extends Throwable>> ERROR_TRANSLATIONS = Map.of(
      "invalid_access_key", () -> new IllegalAccessException("Invalid user id"),
      "invalid_base_currency", () -> new IllegalArgumentException("Invalid base currency"),
      "base_currency_access_restricted", () -> new IllegalStateException("Base currency is not available"),
      "invalid_currency_codes", () -> new IllegalArgumentException("Invalid target currency"));
  private static final Supplier<? extends Throwable> DEFAULT_ERROR = () -> new RuntimeException("Unknown error");

  private static final String PATH = "/v1/latest";

  private final WebClient webClient;

  public ExchangeRateApiClient(final WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<ExchangeRateResponseDto> getCurrentExchangeRate(
      final String userId, final String fromCurrency, final String toCurrency) {

    return webClient.method(GET)
        .uri(uriBuilder -> uriBuilder
            .path(PATH)
            .queryParam("access_key", userId)
            .queryParam("base", fromCurrency)
            .queryParam("symbols", toCurrency)
            .build())
        .accept(APPLICATION_JSON)
        .contentType(APPLICATION_JSON)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, this::handle4xxError)
        .onStatus(HttpStatus::is5xxServerError, this::handle5xxError)
        .bodyToMono(ExchangeRateResponseDto.class);
  }

  private Mono<Throwable> handle4xxError(ClientResponse clientResponse) {
    return clientResponse.body(BodyExtractors.toMono(ExchangeRateErrorDto.class))
        .map(this::translateErrorMessage)
        .flatMap(Mono::error);
  }

  private Mono<Throwable> handle5xxError(ClientResponse clientResponse) {
    return clientResponse.body(BodyExtractors.toMono(ExchangeRateErrorDto.class))
        .map(this::translateErrorMessage)
        .flatMap(Mono::error);
  }

  private Throwable translateErrorMessage(ExchangeRateErrorDto errorDto) {
    final var errorCode = errorDto.getError().getCode();
    return ERROR_TRANSLATIONS.getOrDefault(errorCode, DEFAULT_ERROR).get();
  }
}

package com.github.paulosalonso.currencyconverter.client;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.github.paulosalonso.currencyconverter.client.dto.ExchangeRateErrorDto;
import com.github.paulosalonso.currencyconverter.client.dto.ExchangeRateResponseDto;
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
    final var error = clientResponse.body(BodyExtractors.toMono(ExchangeRateErrorDto.class));

    return error.doOnSuccess(this::logError)
        .flatMap(errorDto ->
            Mono.error(new IllegalArgumentException(errorDto.getError().getCode())));
  }

  private Mono<Throwable> handle5xxError(ClientResponse clientResponse) {
    final var error = clientResponse.body(BodyExtractors.toMono(ExchangeRateErrorDto.class));

    return error.doOnSuccess(this::logError)
        .flatMap(errorDto ->
            Mono.error(new RuntimeException(errorDto.getError().getCode())));
  }

  private void logError(ExchangeRateErrorDto exchangeRateErrorDto) {
    log.error("Error getting exchange rate: {} - {}",
        exchangeRateErrorDto.getError().getCode(), exchangeRateErrorDto.getError().getMessage());
  }
}

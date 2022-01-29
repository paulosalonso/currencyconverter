package com.github.paulosalonso.currencyconverter.repository;

import static java.time.ZoneOffset.UTC;

import com.github.paulosalonso.currencyconverter.repository.http.ExchangeRateApiClient;
import com.github.paulosalonso.currencyconverter.repository.http.dto.ExchangeRateResponseDto;
import com.github.paulosalonso.currencyconverter.model.Currency;
import com.github.paulosalonso.currencyconverter.model.ExchangeRate;
import com.github.paulosalonso.currencyconverter.model.ExchangeRequest;
import com.github.paulosalonso.currencyconverter.service.port.ExchangeRatePort;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ExchangeRateApiPort implements ExchangeRatePort {

  private static final Map<String, String> ERROR_TRANSLATIONS = Map.of(
      "invalid_access_key", "Invalid user id",
      "invalid_base_currency", "Invalid base currency",
      "base_currency_access_restricted", "Base currency is not available",
      "invalid_currency_codes", "Invalid target currency");

  private final ExchangeRateApiClient exchangeRateApiClient;

  public ExchangeRateApiPort(final ExchangeRateApiClient exchangeRateApiClient) {
    this.exchangeRateApiClient = exchangeRateApiClient;
  }

  @Override
  public Mono<ExchangeRate> getCurrentExchangeRate(ExchangeRequest request) {
    final var userId = request.getUserId();
    final var fromCurrency = request.getFromCurrency().name();
    final var toCurrency = request.getToCurrency().name();

    return exchangeRateApiClient.getCurrentExchangeRate(userId, fromCurrency,toCurrency)
        .map(exchangeRateResponseDto -> map(request.getToCurrency(), exchangeRateResponseDto))
        .onErrorResume(this::mapException);
  }

  private ExchangeRate map(final Currency toCurrency,
      final ExchangeRateResponseDto exchangeRateResponseDto) {

    final var instant = Instant.ofEpochSecond(exchangeRateResponseDto.getTimestamp());
    final var dateTime = ZonedDateTime.ofInstant(instant, UTC);

    return ExchangeRate.builder()
        .dateTime(dateTime)
        .fromCurrency(Currency.valueOf(exchangeRateResponseDto.getBase()))
        .toCurrency(toCurrency)
        .rate(exchangeRateResponseDto.getRates().get(toCurrency.name()))
        .build();
  }

  private Mono<ExchangeRate> mapException(Throwable throwable) {
    final var exceptionTranslation = ERROR_TRANSLATIONS.get(throwable.getMessage());

    if (exceptionTranslation == null) {
      return Mono.error(new RuntimeException("Unknown error"));
    }

    return Mono.error(new IllegalArgumentException(exceptionTranslation));
  }
}

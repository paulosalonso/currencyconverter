package com.github.paulosalonso.currencyconverter.repository;

import static com.github.paulosalonso.currencyconverter.repository.mapper.ExchangeTransactionEntityMapper.toEntity;
import static java.time.ZoneOffset.UTC;

import com.github.paulosalonso.currencyconverter.model.Currency;
import com.github.paulosalonso.currencyconverter.model.ExchangeRate;
import com.github.paulosalonso.currencyconverter.model.ExchangeRequest;
import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import com.github.paulosalonso.currencyconverter.repository.database.ExchangeTransactionEntityRepository;
import com.github.paulosalonso.currencyconverter.repository.http.ExchangeRateApiClient;
import com.github.paulosalonso.currencyconverter.repository.http.dto.ExchangeRateResponseDto;
import com.github.paulosalonso.currencyconverter.repository.mapper.ExchangeTransactionEntityMapper;
import com.github.paulosalonso.currencyconverter.service.port.ExchangePort;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ExchangeRepository implements ExchangePort {

  private final ExchangeRateApiClient exchangeRateApiClient;
  private final ExchangeTransactionEntityRepository exchangeTransactionEntityRepository;

  public ExchangeRepository(final ExchangeRateApiClient exchangeRateApiClient,
      final ExchangeTransactionEntityRepository exchangeTransactionEntityRepository) {
    this.exchangeRateApiClient = exchangeRateApiClient;
    this.exchangeTransactionEntityRepository = exchangeTransactionEntityRepository;
  }

  @Override
  public Mono<ExchangeRate> getCurrentExchangeRate(ExchangeRequest request) {
    final var userId = request.getUserId();
    final var fromCurrency = request.getFromCurrency().name();
    final var toCurrency = request.getToCurrency().name();

    return exchangeRateApiClient.getCurrentExchangeRate(userId, fromCurrency,toCurrency)
        .map(exchangeRateResponseDto -> map(request.getToCurrency(), exchangeRateResponseDto));
  }

  @Override
  public Mono<ExchangeTransaction> save(ExchangeTransaction transaction) {
    return exchangeTransactionEntityRepository.save(toEntity(transaction))
        .map(ExchangeTransactionEntityMapper::toModel);
  }

  private ExchangeRate map(final Currency toCurrency,
      final ExchangeRateResponseDto exchangeRateResponseDto) {

    final var instant = Instant.ofEpochSecond(exchangeRateResponseDto.getTimestamp());
    final var dateTime = ZonedDateTime.ofInstant(instant, UTC);
    final var rate = BigDecimal.valueOf(exchangeRateResponseDto.getRates().get(toCurrency.name()));

    return ExchangeRate.builder()
        .dateTime(dateTime)
        .fromCurrency(Currency.valueOf(exchangeRateResponseDto.getBase()))
        .toCurrency(toCurrency)
        .rate(rate)
        .build();
  }
}

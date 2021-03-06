package com.github.paulosalonso.currencyconverter.repository;

import static com.github.paulosalonso.currencyconverter.repository.mapper.ExchangeRateResponseDtoMapper.toModel;
import static com.github.paulosalonso.currencyconverter.repository.mapper.ExchangeTransactionEntityMapper.toEntity;

import com.github.paulosalonso.currencyconverter.model.ExchangeRate;
import com.github.paulosalonso.currencyconverter.model.ExchangeRequest;
import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import com.github.paulosalonso.currencyconverter.repository.database.ExchangeTransactionEntityRepository;
import com.github.paulosalonso.currencyconverter.repository.http.ExchangeRateApiClient;
import com.github.paulosalonso.currencyconverter.repository.mapper.ExchangeTransactionEntityMapper;
import com.github.paulosalonso.currencyconverter.service.port.ExchangePort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
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
    final var fromCurrency = request.getFromCurrency();
    final var toCurrency = request.getToCurrency();

    return exchangeRateApiClient.getCurrentExchangeRate(userId, fromCurrency,toCurrency)
        .map(exchangeRateResponseDto -> toModel(request.getToCurrency(), exchangeRateResponseDto));
  }

  @Override
  public Mono<ExchangeTransaction> save(ExchangeTransaction transaction) {
    return exchangeTransactionEntityRepository.save(toEntity(transaction))
        .map(ExchangeTransactionEntityMapper::toModel);
  }

  @Override
  public Flux<ExchangeTransaction> findAllTransactionsByUserId(String userId, int page, int pageSize) {
    return exchangeTransactionEntityRepository.findAllByUserId(userId, PageRequest.of(page, pageSize))
        .map(ExchangeTransactionEntityMapper::toModel);
  }
}

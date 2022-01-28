package com.github.paulosalonso.currencyconverter.service;

import com.github.paulosalonso.currencyconverter.model.ExchangeRate;
import com.github.paulosalonso.currencyconverter.model.ExchangeRateRequest;
import com.github.paulosalonso.currencyconverter.model.ExchangeRateTransaction;
import com.github.paulosalonso.currencyconverter.service.port.ExchangeRatePort;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ConverterService {

  private final ExchangeRatePort exchangeRatePort;

  public ConverterService(final ExchangeRatePort exchangeRatePort) {
    this.exchangeRatePort = exchangeRatePort;
  }

  public Mono<ExchangeRateTransaction> convert(final ExchangeRateRequest request) {
    log.info("Starting conversion: {}", request);

    return exchangeRatePort.getCurrentExchangeRate(request)
        .map(exchangeRate -> map(request, exchangeRate));
  }

  private ExchangeRateTransaction map(ExchangeRateRequest request, ExchangeRate exchangeRate) {

    final var rate = BigDecimal.valueOf(exchangeRate.getRate());
    final var convertedAmount = request.getAmount().multiply(rate);

    return ExchangeRateTransaction.builder()
        .id(UUID.randomUUID())
        .userId(request.getUserId())
        .fromCurrency(request.getFromCurrency())
        .originalAmount(request.getAmount())
        .toCurrency(request.getToCurrency())
        .convertedAmount(convertedAmount)
        .rate(rate)
        .dateTime(exchangeRate.getDateTime())
        .build();
  }
}

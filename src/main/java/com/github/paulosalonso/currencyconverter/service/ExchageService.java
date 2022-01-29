package com.github.paulosalonso.currencyconverter.service;

import com.github.paulosalonso.currencyconverter.model.ExchangeRate;
import com.github.paulosalonso.currencyconverter.model.ExchangeRequest;
import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import com.github.paulosalonso.currencyconverter.service.port.ExchangePort;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ExchageService {

  private final ExchangePort exchangePort;

  public ExchageService(final ExchangePort exchangePort) {
    this.exchangePort = exchangePort;
  }

  public Mono<ExchangeTransaction> convert(final ExchangeRequest request) {
    log.info("Starting conversion: {}", request);

    return exchangePort.getCurrentExchangeRate(request)
        .map(exchangeRate -> map(request, exchangeRate));
  }

  private ExchangeTransaction map(ExchangeRequest request, ExchangeRate exchangeRate) {

    final var rate = BigDecimal.valueOf(exchangeRate.getRate());
    final var convertedAmount = request.getAmount().multiply(rate);

    return ExchangeTransaction.builder()
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

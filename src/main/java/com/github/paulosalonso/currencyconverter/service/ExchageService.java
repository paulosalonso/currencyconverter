package com.github.paulosalonso.currencyconverter.service;

import com.github.paulosalonso.currencyconverter.model.ExchangeRate;
import com.github.paulosalonso.currencyconverter.model.ExchangeRequest;
import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import com.github.paulosalonso.currencyconverter.service.port.ExchangePort;
import java.math.BigDecimal;
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

  public Mono<ExchangeTransaction> exchange(final ExchangeRequest request) {
    log.info("Starting conversion: {}", request);

    return exchangePort.getCurrentExchangeRate(request)
        .map(exchangeRate -> createTransaction(request, exchangeRate))
        .flatMap(exchangePort::save)
        .doOnSuccess(this::logTransaction);
  }

  private ExchangeTransaction createTransaction(
      ExchangeRequest request, ExchangeRate exchangeRate) {

    final var convertedAmount = convert(request, exchangeRate);

    return ExchangeTransaction.builder()
        .userId(request.getUserId())
        .fromCurrency(request.getFromCurrency())
        .originalAmount(request.getAmount())
        .toCurrency(request.getToCurrency())
        .convertedAmount(convertedAmount)
        .rate(exchangeRate.getRate())
        .dateTime(exchangeRate.getDateTime())
        .build();
  }

  private BigDecimal convert(ExchangeRequest request, ExchangeRate exchangeRate) {
    return request.getAmount().multiply(exchangeRate.getRate());
  }

  private void logTransaction(ExchangeTransaction exchangeTransaction) {
    log.info("Successful conversion: {}", exchangeTransaction);
  }
}

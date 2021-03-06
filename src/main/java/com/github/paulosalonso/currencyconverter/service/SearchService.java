package com.github.paulosalonso.currencyconverter.service;

import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import com.github.paulosalonso.currencyconverter.service.port.ExchangePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class SearchService {

  private final ExchangePort exchangePort;

  public SearchService(final ExchangePort exchangePort) {
    this.exchangePort = exchangePort;
  }

  public Flux<ExchangeTransaction> findAllTransactionsByUserId(final String userId, int page, int pageSize) {
    log.info("Searching transactions page {} with size {} for user {}", page, pageSize, userId);
    return exchangePort.findAllTransactionsByUserId(userId, page, pageSize);
  }
}

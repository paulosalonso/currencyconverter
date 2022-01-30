package com.github.paulosalonso.currencyconverter.service.port;

import com.github.paulosalonso.currencyconverter.model.ExchangeRate;
import com.github.paulosalonso.currencyconverter.model.ExchangeRequest;
import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ExchangePort {

  Mono<ExchangeRate> getCurrentExchangeRate(ExchangeRequest request);

  Mono<ExchangeTransaction> save(ExchangeTransaction transaction);

  Flux<ExchangeTransaction> findAllTransactionsByUserId(String userId);
}

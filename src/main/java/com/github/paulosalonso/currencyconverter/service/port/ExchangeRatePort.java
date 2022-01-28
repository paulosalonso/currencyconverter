package com.github.paulosalonso.currencyconverter.service.port;

import com.github.paulosalonso.currencyconverter.model.ExchangeRate;
import com.github.paulosalonso.currencyconverter.model.ExchangeRequest;
import reactor.core.publisher.Mono;

public interface ExchangeRatePort {

  Mono<ExchangeRate> getCurrentExchangeRate(ExchangeRequest request);
}

package com.github.paulosalonso.currencyconverter.service.port;

import com.github.paulosalonso.currencyconverter.model.ExchangeRate;
import com.github.paulosalonso.currencyconverter.model.ExchangeRateRequest;
import reactor.core.publisher.Mono;

public interface ExchangeRatePort {

  Mono<ExchangeRate> getCurrentExchangeRate(ExchangeRateRequest request);
}

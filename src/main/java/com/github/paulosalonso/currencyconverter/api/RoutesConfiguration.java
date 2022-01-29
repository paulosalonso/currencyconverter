package com.github.paulosalonso.currencyconverter.api;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RoutesConfiguration {

  @Bean
  public RouterFunction<ServerResponse> routeExchangeRequest(ExchangeHandler exchangeHandler) {
    return RouterFunctions.route(
        POST("/v1/exchanges").and(accept(APPLICATION_JSON)),
        exchangeHandler::convert);
  }
}

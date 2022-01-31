package com.github.paulosalonso.currencyconverter.api;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RoutesConfiguration {

  private static final String EXCHANGES_PATH = "/v1/exchanges";

  private final ExchangeHandler exchangeHandler;
  private final SearchHandler searchHandler;

  public RoutesConfiguration(
      final ExchangeHandler exchangeHandler, final SearchHandler searchHandler) {

    this.exchangeHandler = exchangeHandler;
    this.searchHandler = searchHandler;
  }

  @Bean
  @RouterOperations({
      @RouterOperation(
          path = EXCHANGES_PATH,
          method = POST,
          consumes = APPLICATION_JSON_VALUE,
          produces = APPLICATION_JSON_VALUE,
          beanClass = ExchangeHandler.class,
          beanMethod = "handle"),
      @RouterOperation(
          path = EXCHANGES_PATH,
          method = GET,
          consumes = APPLICATION_JSON_VALUE,
          produces = APPLICATION_JSON_VALUE,
          beanClass = SearchHandler.class,
          beanMethod = "handle")
  })
  public RouterFunction<ServerResponse> routeExchangeRequest() {
    return RouterFunctions
        .route(
            POST(EXCHANGES_PATH).and(accept(APPLICATION_JSON)),
            exchangeHandler::handle)
        .andRoute(
            GET(EXCHANGES_PATH).and(accept(APPLICATION_JSON)),
            searchHandler::handle);
  }
}

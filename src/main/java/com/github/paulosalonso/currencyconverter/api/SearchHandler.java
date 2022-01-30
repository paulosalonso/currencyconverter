package com.github.paulosalonso.currencyconverter.api;

import static java.lang.String.format;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;

import com.github.paulosalonso.currencyconverter.api.dto.TransactionDto;
import com.github.paulosalonso.currencyconverter.api.mapper.TransactionDtoMapper;
import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import com.github.paulosalonso.currencyconverter.service.SearchService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SearchHandler {

  private static final String USER_ID_PARAM = "userId";

  private final SearchService searchService;

  public SearchHandler(final SearchService searchService) {
    this.searchService = searchService;
  }

  public Mono<ServerResponse> handle(ServerRequest request) {
    return request.queryParam(USER_ID_PARAM)
        .map(searchService::findAllTransactionsByUserId)
        .map(this::createSuccessResponse)
        .orElse(this.createResponseForRequiredParamNotPresent(USER_ID_PARAM));
  }

  private Mono<ServerResponse> createSuccessResponse(Flux<ExchangeTransaction> response) {
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(response.map(TransactionDtoMapper::toTransactionDto), TransactionDto.class);
  }

  private Mono<ServerResponse> createResponseForRequiredParamNotPresent(String param) {
    final var response = Mono.error(
        new IllegalArgumentException(format("The param '%s' is required", param)));

    return ServerResponse.badRequest()
        .body(fromPublisher(response, Object.class));
  }

}

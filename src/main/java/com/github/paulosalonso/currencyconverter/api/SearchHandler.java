package com.github.paulosalonso.currencyconverter.api;

import static java.lang.String.format;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;

import com.github.paulosalonso.currencyconverter.api.dto.TransactionDto;
import com.github.paulosalonso.currencyconverter.api.mapper.TransactionDtoMapper;
import com.github.paulosalonso.currencyconverter.service.SearchService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class SearchHandler {

  private static final String USER_ID_PARAM = "userId";

  private final SearchService searchService;
  private final ErrorHandler errorHandler;

  public SearchHandler(final SearchService searchService, final ErrorHandler errorHandler) {
    this.searchService = searchService;
    this.errorHandler = errorHandler;
  }

  public Mono<ServerResponse> handle(ServerRequest request) {
    final var userIdOpt = request.queryParam(USER_ID_PARAM);

    if (userIdOpt.isEmpty()) {
      return errorHandler.handle(
          new IllegalArgumentException(format("The param '%s' is required", USER_ID_PARAM)));
    }

    final var transactionDtoFlux = searchService.findAllTransactionsByUserId(userIdOpt.get())
        .map(TransactionDtoMapper::toTransactionDto);

    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(fromPublisher(transactionDtoFlux, TransactionDto.class))
        .onErrorResume(errorHandler::handle);
  }

}

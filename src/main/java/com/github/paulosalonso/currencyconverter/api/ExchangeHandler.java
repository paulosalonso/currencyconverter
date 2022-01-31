package com.github.paulosalonso.currencyconverter.api;

import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;

import com.github.paulosalonso.currencyconverter.api.dto.ExchangeRequestDto;
import com.github.paulosalonso.currencyconverter.api.dto.TransactionDto;
import com.github.paulosalonso.currencyconverter.api.mapper.ExchangeRequestMapper;
import com.github.paulosalonso.currencyconverter.api.mapper.TransactionDtoMapper;
import com.github.paulosalonso.currencyconverter.service.ExchageService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ExchangeHandler {

  private final ExchageService exchageService;

  public ExchangeHandler(final ExchageService exchageService) {
    this.exchageService = exchageService;
  }

  public Mono<ServerResponse> handle(ServerRequest request) {
    final var response = request.bodyToMono(ExchangeRequestDto.class)
        .map(ExchangeRequestMapper::toModel)
        .flatMap(exchageService::exchange)
        .map(TransactionDtoMapper::toTransactionDto);

    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(fromPublisher(response, TransactionDto.class));
  }
}

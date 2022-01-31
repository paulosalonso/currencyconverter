package com.github.paulosalonso.currencyconverter.api;

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
  private final ErrorHandler errorHandler;

  public ExchangeHandler(final ExchageService exchageService, final ErrorHandler errorHandler) {
    this.exchageService = exchageService;
    this.errorHandler = errorHandler;
  }

  public Mono<ServerResponse> handle(ServerRequest request) {
    return request.bodyToMono(ExchangeRequestDto.class)
        .map(ExchangeRequestMapper::toModel)
        .flatMap(exchageService::exchange)
        .map(TransactionDtoMapper::toTransactionDto)
        .flatMap(this::createResponse)
        .onErrorResume(errorHandler::handle);
  }

  private Mono<ServerResponse> createResponse(TransactionDto transactionDto) {
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(transactionDto);
  }
}

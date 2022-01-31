package com.github.paulosalonso.currencyconverter.api;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import com.github.paulosalonso.currencyconverter.api.dto.ExchangeRequestDto;
import com.github.paulosalonso.currencyconverter.api.dto.TransactionDto;
import com.github.paulosalonso.currencyconverter.api.mapper.ExchangeRequestMapper;
import com.github.paulosalonso.currencyconverter.api.mapper.TransactionDtoMapper;
import com.github.paulosalonso.currencyconverter.service.ExchageService;
import javax.validation.Validator;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ExchangeHandler {

  private static final String FIELD_VALIDATION_MESSAGE = "property '%s' %s";
  private static final String VALIDATION_ERROR_MESSAGE = "Invalid data. Violations: %s";

  private final ExchageService exchageService;
  private final ErrorHandler errorHandler;
  private final Validator validator;

  public ExchangeHandler(final ExchageService exchageService,
      final ErrorHandler errorHandler, final Validator validator) {

    this.exchageService = exchageService;
    this.errorHandler = errorHandler;
    this.validator = validator;
  }

  public Mono<ServerResponse> handle(ServerRequest request) {
    return request.bodyToMono(ExchangeRequestDto.class)
        .flatMap(this::validate)
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

  private Mono<ExchangeRequestDto> validate(ExchangeRequestDto exchangeRequestDto) {
    final var violations = validator.validate(exchangeRequestDto);

    if (!violations.isEmpty()) {
      final var error = new IllegalArgumentException(format(
          VALIDATION_ERROR_MESSAGE,
          violations.stream()
              .map(violation -> format(
                  FIELD_VALIDATION_MESSAGE,
                  violation.getPropertyPath(),
                  violation.getMessage()))
              .collect(joining(", "))));

      return Mono.error(error);
    }

    return Mono.just(exchangeRequestDto);
  }
}

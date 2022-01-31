package com.github.paulosalonso.currencyconverter.api;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import com.github.paulosalonso.currencyconverter.api.dto.ErrorDto;
import com.github.paulosalonso.currencyconverter.api.dto.ExchangeRequestDto;
import com.github.paulosalonso.currencyconverter.api.dto.TransactionDto;
import com.github.paulosalonso.currencyconverter.api.mapper.ExchangeRequestMapper;
import com.github.paulosalonso.currencyconverter.api.mapper.TransactionDtoMapper;
import com.github.paulosalonso.currencyconverter.service.ExchageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

  @Operation(
      operationId = "exchange",
      summary = "Perform currency exchange",
      tags = { "Exchange" },
      requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = ExchangeRequestDto.class))),
      responses = {
          @ApiResponse(responseCode = "200", description = "Exchange was performed", content = @Content(schema = @Schema(implementation = TransactionDto.class))),
          @ApiResponse(responseCode = "400", description = "Request body or its data is not valid", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
          @ApiResponse(responseCode = "401", description = "User is not authorized", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
          @ApiResponse(responseCode = "403", description = "User is not allowed to perform exchange for sent base currency", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
          @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
      })
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

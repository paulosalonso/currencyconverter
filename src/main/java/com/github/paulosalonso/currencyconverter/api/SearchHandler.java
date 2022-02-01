package com.github.paulosalonso.currencyconverter.api;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;
import static java.lang.String.format;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;

import com.github.paulosalonso.currencyconverter.api.dto.ErrorDto;
import com.github.paulosalonso.currencyconverter.api.dto.TransactionDto;
import com.github.paulosalonso.currencyconverter.api.mapper.TransactionDtoMapper;
import com.github.paulosalonso.currencyconverter.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
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
  private final int defaultPageSize;

  public SearchHandler(
      final SearchService searchService,
      final ErrorHandler errorHandler,
      @Value("${exchange.get.defaultPageSize}") int defaultPageSize) {

    this.searchService = searchService;
    this.errorHandler = errorHandler;
    this.defaultPageSize = defaultPageSize;
  }

  @Operation(
      operationId = "findAllTransactionsByUserId",
      summary = "Get all transactions of an user",
      tags = { "Exchange" },
      parameters = {
          @Parameter(in = QUERY, name = "userId", required = true, description = "Id of user"),
          @Parameter(in = QUERY, name = "page", description = "Page number (zero based)", schema = @Schema(defaultValue = "0")),
          @Parameter(in = QUERY, name = "pageSize", description = "Page size", schema = @Schema(defaultValue = "50"))
      },
      responses = {
          @ApiResponse(responseCode = "200", description = "Transactions are returned", content = @Content(schema = @Schema(implementation = TransactionDto.class))),
          @ApiResponse(responseCode = "400", description = "User id was not sent", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
          @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
      })
  public Mono<ServerResponse> handle(ServerRequest request) {
    final var userIdOpt = request.queryParam(USER_ID_PARAM);

    if (userIdOpt.isEmpty()) {
      return errorHandler.handle(
          new IllegalArgumentException(format("The param '%s' is required", USER_ID_PARAM)));
    }

    try {
      final var page = request.queryParam("page")
          .map(value -> mapIntParameter(value, "page"))
          .orElse(0);

      final var pageSize = request.queryParam("pageSize")
          .map(value -> mapIntParameter(value, "pageSize"))
          .orElse(defaultPageSize);

      final var transactionDtoFlux =
          searchService.findAllTransactionsByUserId(userIdOpt.get(),page, pageSize)
              .map(TransactionDtoMapper::toTransactionDto);

      return ServerResponse.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(fromPublisher(transactionDtoFlux, TransactionDto.class))
          .onErrorResume(errorHandler::handle);
    } catch (IllegalArgumentException e) {
      return errorHandler.handle(e);
    }
  }

  private Integer mapIntParameter(String value, String parameter) {
    try {
      return Integer.valueOf(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          format("The value of property '%s' is invalid. It should be an integer value.", parameter));
    }
  }

}

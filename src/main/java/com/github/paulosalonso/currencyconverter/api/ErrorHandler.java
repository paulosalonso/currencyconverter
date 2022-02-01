package com.github.paulosalonso.currencyconverter.api;

import static com.github.paulosalonso.currencyconverter.api.mapper.ThrowableMapper.toErrorDto;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ErrorHandler {

  private static final Map<Class<? extends Throwable>, HttpStatus> STATUS_MAP = Map.of(
      IllegalArgumentException.class, BAD_REQUEST,
      IllegalAccessException.class, UNAUTHORIZED,
      IllegalStateException.class, FORBIDDEN);

  public Mono<ServerResponse> handle(Throwable throwable) {
    log.error(format("Handling error: %s", throwable.getMessage()), throwable);

    final var status = STATUS_MAP.getOrDefault(throwable.getClass(), INTERNAL_SERVER_ERROR);
    final var errorDto = toErrorDto(throwable, status);

    return ServerResponse.status(HttpStatus.valueOf(errorDto.getStatus()))
        .contentType(APPLICATION_JSON)
        .bodyValue(errorDto);
  }
}

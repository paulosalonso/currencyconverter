package com.github.paulosalonso.currencyconverter.api.mapper;

import static java.time.ZoneOffset.UTC;
import static lombok.AccessLevel.PRIVATE;

import com.github.paulosalonso.currencyconverter.api.dto.ErrorDto;
import java.time.ZonedDateTime;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = PRIVATE)
public class ThrowableMapper {

  public static ErrorDto toErrorDto(Throwable error, HttpStatus status) {
    return ErrorDto.builder()
        .status(status.value())
        .message(error.getMessage())
        .timestamp(ZonedDateTime.now(UTC))
        .build();
  }
}

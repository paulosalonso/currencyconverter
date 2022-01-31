package com.github.paulosalonso.currencyconverter.repository.mapper;

import static java.time.ZoneOffset.UTC;
import static lombok.AccessLevel.PRIVATE;

import com.github.paulosalonso.currencyconverter.model.ExchangeRate;
import com.github.paulosalonso.currencyconverter.repository.http.dto.ExchangeRateResponseDto;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class ExchangeRateResponseDtoMapper {

  public static ExchangeRate toModel(final String toCurrency,
      final ExchangeRateResponseDto exchangeRateResponseDto) {

    final var instant = Instant.ofEpochSecond(exchangeRateResponseDto.getTimestamp());
    final var dateTime = ZonedDateTime.ofInstant(instant, UTC);
    final var rate = BigDecimal.valueOf(exchangeRateResponseDto.getRates().get(toCurrency));

    return ExchangeRate.builder()
        .dateTime(dateTime)
        .fromCurrency(exchangeRateResponseDto.getBase())
        .toCurrency(toCurrency)
        .rate(rate)
        .build();
  }

}

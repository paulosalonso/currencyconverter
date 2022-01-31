package com.github.paulosalonso.currencyconverter.repository.mapper;

import static com.github.paulosalonso.currencyconverter.repository.mapper.ExchangeRateResponseDtoMapper.toModel;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.paulosalonso.currencyconverter.repository.http.dto.ExchangeRateResponseDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ExchangeRateResponseDtoMapperTest {

  @Test
  void givenAExchangeRateResponseDtoWhenMapThenReturnExchangeRateInstance() {
    final var fromCurrency = "EUR";
    final var toCurrency = "BRL";

    final var dto = ExchangeRateResponseDto.builder()
        .success(true)
        .timestamp(ZonedDateTime.now(UTC).toEpochSecond())
        .date(LocalDate.now())
        .base(fromCurrency)
        .rates(Map.of(toCurrency, 5.0))
        .build();

    final var exchangeRate = toModel(toCurrency, dto);

    assertThat(exchangeRate.getDateTime().toEpochSecond()).isEqualTo(dto.getTimestamp());
    assertThat(exchangeRate.getFromCurrency()).isEqualTo(fromCurrency);
    assertThat(exchangeRate.getToCurrency()).isEqualTo(toCurrency);
    assertThat(exchangeRate.getRate())
        .isEqualTo(BigDecimal.valueOf(dto.getRates().get(toCurrency)));
  }

}

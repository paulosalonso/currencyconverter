package com.github.paulosalonso.currencyconverter.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.paulosalonso.currencyconverter.api.dto.ExchangeRequestDto;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ExchangeRequestMapperTest {

  @Test
  void givenAnExchangeRequestDtoWhenMapThenReturnExchangeRequestInstance() {
    final var requestDto = ExchangeRequestDto.builder()
        .userId("user-id")
        .fromCurrency("EUR")
        .amount(BigDecimal.TEN)
        .toCurrency("BRL")
        .build();

    final var exchangeRequest = ExchangeRequestMapper.toModel(requestDto);

    assertThat(exchangeRequest.getUserId()).isEqualTo(requestDto.getUserId());
    assertThat(exchangeRequest.getFromCurrency()).isEqualTo("EUR");
    assertThat(exchangeRequest.getAmount()).isEqualTo(requestDto.getAmount());
    assertThat(exchangeRequest.getToCurrency()).isEqualTo("BRL");
  }
}

package com.github.paulosalonso.currencyconverter.api.mapper;

import static com.github.paulosalonso.currencyconverter.model.Currency.BRL;
import static com.github.paulosalonso.currencyconverter.model.Currency.EUR;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.paulosalonso.currencyconverter.api.dto.ExchangeRequestDto;
import com.github.paulosalonso.currencyconverter.model.Currency;
import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;
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
    assertThat(exchangeRequest.getFromCurrency()).isEqualTo(EUR);
    assertThat(exchangeRequest.getAmount()).isEqualTo(requestDto.getAmount());
    assertThat(exchangeRequest.getToCurrency()).isEqualTo(BRL);
  }

  @Test
  void givenAnExceptionTransactionWhenMapThenReturnExchangeResponseDto() {
    final var exchangeTransaction = ExchangeTransaction.builder()
        .id(UUID.randomUUID())
        .userId("user-id")
        .fromCurrency(EUR)
        .originalAmount(BigDecimal.ONE)
        .toCurrency(BRL)
        .convertedAmount(BigDecimal.TEN)
        .rate(BigDecimal.ZERO)
        .dateTime(ZonedDateTime.now(UTC))
        .build();

    final var responseDto = ExchangeRequestMapper.toResponseDto(exchangeTransaction);

    assertThat(responseDto.getId()).isEqualTo(exchangeTransaction.getId().toString());
    assertThat(responseDto.getUserId()).isEqualTo(exchangeTransaction.getUserId());
    assertThat(responseDto.getFromCurrency()).isEqualTo(exchangeTransaction.getFromCurrency().name());
    assertThat(responseDto.getOriginalAmount()).isEqualTo(exchangeTransaction.getOriginalAmount());
    assertThat(responseDto.getToCurrency()).isEqualTo(exchangeTransaction.getToCurrency().name());
    assertThat(responseDto.getConvertedAmount()).isEqualTo(exchangeTransaction.getConvertedAmount());
    assertThat(responseDto.getRate()).isEqualTo(exchangeTransaction.getRate());
    assertThat(responseDto.getDateTime()).isEqualTo(exchangeTransaction.getDateTime());
  }
}

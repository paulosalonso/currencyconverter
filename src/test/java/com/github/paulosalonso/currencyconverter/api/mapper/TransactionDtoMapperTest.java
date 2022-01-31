package com.github.paulosalonso.currencyconverter.api.mapper;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TransactionDtoMapperTest {

  @Test
  void givenAnExchangeTransactionWhenMapThenReturnExchangeResponseDto() {
    final var exchangeTransaction = ExchangeTransaction.builder()
        .id(UUID.randomUUID())
        .userId("user-id")
        .fromCurrency("EUR")
        .originalAmount(BigDecimal.ONE)
        .toCurrency("BRL")
        .convertedAmount(BigDecimal.TEN)
        .rate(BigDecimal.ZERO)
        .dateTime(ZonedDateTime.now(UTC))
        .build();

    final var responseDto = TransactionDtoMapper.toTransactionDto(exchangeTransaction);

    assertThat(responseDto.getId()).isEqualTo(exchangeTransaction.getId().toString());
    assertThat(responseDto.getUserId()).isEqualTo(exchangeTransaction.getUserId());
    assertThat(responseDto.getFromCurrency()).isEqualTo(exchangeTransaction.getFromCurrency());
    assertThat(responseDto.getOriginalAmount()).isEqualTo(exchangeTransaction.getOriginalAmount());
    assertThat(responseDto.getToCurrency()).isEqualTo(exchangeTransaction.getToCurrency());
    assertThat(responseDto.getConvertedAmount()).isEqualTo(exchangeTransaction.getConvertedAmount());
    assertThat(responseDto.getRate()).isEqualTo(exchangeTransaction.getRate());
    assertThat(responseDto.getDateTime()).isEqualTo(exchangeTransaction.getDateTime());
  }
}

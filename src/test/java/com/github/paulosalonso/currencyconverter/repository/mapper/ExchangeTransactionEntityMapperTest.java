package com.github.paulosalonso.currencyconverter.repository.mapper;

import static com.github.paulosalonso.currencyconverter.model.Currency.BRL;
import static com.github.paulosalonso.currencyconverter.model.Currency.EUR;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import com.github.paulosalonso.currencyconverter.repository.database.entity.ExchangeTransactionEntity;
import com.github.paulosalonso.currencyconverter.repository.mapper.ExchangeTransactionEntityMapper;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class ExchangeTransactionEntityMapperTest {

  @Test
  void givenAEnxhangeTransactionWhenMapThenReturnExchangeTransactionalEntityInstance() {
    final var exchangeTransaction = ExchangeTransaction.builder()
        .id(UUID.randomUUID())
        .userId("user-id")
        .fromCurrency(EUR)
        .originalAmount(BigDecimal.ZERO)
        .toCurrency(BRL)
        .convertedAmount(BigDecimal.ONE)
        .rate(BigDecimal.TEN)
        .dateTime(ZonedDateTime.now(UTC))
        .build();

    final var entity = ExchangeTransactionEntityMapper.toEntity(exchangeTransaction);

    assertThat(entity.getId()).isNull();
    assertThat(entity.getUserId()).isEqualTo(exchangeTransaction.getUserId());
    assertThat(entity.getFromCurrency()).isEqualTo(exchangeTransaction.getFromCurrency());
    assertThat(entity.getOriginalAmount()).isEqualTo(exchangeTransaction.getOriginalAmount());
    assertThat(entity.getToCurrency()).isEqualTo(exchangeTransaction.getToCurrency());
    assertThat(entity.getConvertedAmount()).isEqualTo(exchangeTransaction.getConvertedAmount());
    assertThat(entity.getRate()).isEqualTo(exchangeTransaction.getRate());
    assertThat(entity.getTimestamp()).isEqualTo(exchangeTransaction.getDateTime().toInstant());
  }

  @Test
  void givenAEnxhangeTransactionEntityWhenMapThenReturnExchangeTransactionalInstance() {
    final var exchangeTransactionEntity = ExchangeTransactionEntity.builder()
        .id(UUID.randomUUID().toString())
        .userId("user-id")
        .fromCurrency(EUR)
        .originalAmount(BigDecimal.ZERO)
        .toCurrency(BRL)
        .convertedAmount(BigDecimal.ONE)
        .rate(BigDecimal.TEN)
        .timestamp(Instant.now(Clock.systemUTC()))
        .build();

    final var exchangeTransaction = ExchangeTransactionEntityMapper.toModel(exchangeTransactionEntity);

    assertThat(exchangeTransaction.getId()).isEqualTo(UUID.fromString(exchangeTransactionEntity.getId()));
    assertThat(exchangeTransaction.getUserId()).isEqualTo(exchangeTransactionEntity.getUserId());
    assertThat(exchangeTransaction.getFromCurrency()).isEqualTo(exchangeTransactionEntity.getFromCurrency());
    assertThat(exchangeTransaction.getOriginalAmount()).isEqualTo(exchangeTransactionEntity.getOriginalAmount());
    assertThat(exchangeTransaction.getToCurrency()).isEqualTo(exchangeTransactionEntity.getToCurrency());
    assertThat(exchangeTransaction.getConvertedAmount()).isEqualTo(exchangeTransactionEntity.getConvertedAmount());
    assertThat(exchangeTransaction.getRate()).isEqualTo(exchangeTransactionEntity.getRate());
    assertThat(exchangeTransaction.getDateTime())
        .isEqualTo(ZonedDateTime.ofInstant(exchangeTransactionEntity.getTimestamp(), UTC));
  }

}

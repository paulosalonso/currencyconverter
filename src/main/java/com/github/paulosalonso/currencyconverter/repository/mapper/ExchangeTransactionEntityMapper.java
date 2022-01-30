package com.github.paulosalonso.currencyconverter.repository.mapper;

import static java.time.ZoneOffset.UTC;
import static lombok.AccessLevel.PRIVATE;

import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import com.github.paulosalonso.currencyconverter.repository.database.entity.ExchangeTransactionEntity;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class ExchangeTransactionEntityMapper {

  public static ExchangeTransactionEntity toEntity(ExchangeTransaction model) {
    return ExchangeTransactionEntity.builder()
        .userId(model.getUserId())
        .fromCurrency(model.getFromCurrency())
        .originalAmount(model.getOriginalAmount())
        .toCurrency(model.getToCurrency())
        .rate(model.getRate())
        .convertedAmount(model.getConvertedAmount())
        .timestamp(model.getDateTime().toInstant())
        .build();
  }

  public static ExchangeTransaction toModel(ExchangeTransactionEntity entity) {
    return ExchangeTransaction.builder()
        .id(UUID.fromString(entity.getId()))
        .userId(entity.getUserId())
        .fromCurrency(entity.getFromCurrency())
        .originalAmount(entity.getOriginalAmount())
        .toCurrency(entity.getToCurrency())
        .convertedAmount(entity.getConvertedAmount())
        .rate(entity.getRate())
        .dateTime(ZonedDateTime.ofInstant(entity.getTimestamp(), UTC))
        .build();
  }

}

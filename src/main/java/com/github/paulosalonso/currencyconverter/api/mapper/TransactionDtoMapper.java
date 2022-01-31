package com.github.paulosalonso.currencyconverter.api.mapper;

import static lombok.AccessLevel.PRIVATE;

import com.github.paulosalonso.currencyconverter.api.dto.TransactionDto;
import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class TransactionDtoMapper {

  public static TransactionDto toTransactionDto(ExchangeTransaction transaction) {
    return TransactionDto.builder()
        .id(transaction.getId().toString())
        .userId(transaction.getUserId())
        .fromCurrency(transaction.getFromCurrency())
        .originalAmount(transaction.getOriginalAmount())
        .toCurrency(transaction.getToCurrency())
        .convertedAmount(transaction.getConvertedAmount())
        .rate(transaction.getRate())
        .dateTime(transaction.getDateTime())
        .build();
  }

}

package com.github.paulosalonso.currencyconverter.api.mapper;

import static lombok.AccessLevel.PRIVATE;

import com.github.paulosalonso.currencyconverter.api.dto.ExchangeResponseDto;
import com.github.paulosalonso.currencyconverter.api.dto.ExchangeRequestDto;
import com.github.paulosalonso.currencyconverter.model.ExchangeRequest;
import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class ExchangeRequestMapper {

  public static ExchangeRequest toModel(ExchangeRequestDto dto) {
    return ExchangeRequest.of(
        dto.getUserId(),
        dto.getFromCurrency(),
        dto.getAmount(),
        dto.getToCurrency());
  }

  public static ExchangeResponseDto toResponseDto(ExchangeTransaction transaction) {
    return ExchangeResponseDto.builder()
        .id(transaction.getId().toString())
        .userId(transaction.getUserId())
        .fromCurrency(transaction.getFromCurrency().name())
        .originalAmount(transaction.getOriginalAmount())
        .toCurrency(transaction.getToCurrency().name())
        .convertedAmount(transaction.getConvertedAmount())
        .rate(transaction.getRate())
        .dateTime(transaction.getDateTime())
        .build();
  }

}

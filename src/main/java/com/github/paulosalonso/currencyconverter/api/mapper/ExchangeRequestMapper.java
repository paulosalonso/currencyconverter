package com.github.paulosalonso.currencyconverter.api.mapper;

import static lombok.AccessLevel.PRIVATE;

import com.github.paulosalonso.currencyconverter.api.dto.ExchangeRequestDto;
import com.github.paulosalonso.currencyconverter.model.ExchangeRequest;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class ExchangeRequestMapper {

  public static ExchangeRequest toModel(ExchangeRequestDto dto) {
    return ExchangeRequest.builder()
        .userId(dto.getUserId())
        .fromCurrency(dto.getFromCurrency())
        .amount(dto.getAmount())
        .toCurrency(dto.getToCurrency())
        .build();
  }
}

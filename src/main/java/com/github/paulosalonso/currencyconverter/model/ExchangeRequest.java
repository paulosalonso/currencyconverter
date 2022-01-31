package com.github.paulosalonso.currencyconverter.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class ExchangeRequest {
  private final String userId;
  private final String fromCurrency;
  private final BigDecimal amount;
  private final String toCurrency;
}

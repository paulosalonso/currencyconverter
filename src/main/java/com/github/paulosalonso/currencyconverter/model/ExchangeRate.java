package com.github.paulosalonso.currencyconverter.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class ExchangeRate {

  private final ZonedDateTime dateTime;
  private final String fromCurrency;
  private final String toCurrency;
  private final BigDecimal rate;
}

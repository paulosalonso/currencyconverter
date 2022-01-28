package com.github.paulosalonso.currencyconverter.model;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class ExchangeRate {

  private ZonedDateTime dateTime;
  private Currency fromCurrency;
  private Currency toCurrency;
  private double rate;
}

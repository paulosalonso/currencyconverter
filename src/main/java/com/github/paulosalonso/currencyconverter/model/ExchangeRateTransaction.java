package com.github.paulosalonso.currencyconverter.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;
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
public class ExchangeRateTransaction {
  private UUID id;
  private String userId;
  private Currency fromCurrency;
  private BigDecimal originalAmount;
  private Currency toCurrency;
  private BigDecimal convertedAmount;
  private BigDecimal rate;
  private ZonedDateTime dateTime;
}

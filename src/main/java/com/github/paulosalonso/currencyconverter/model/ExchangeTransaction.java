package com.github.paulosalonso.currencyconverter.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class ExchangeTransaction {
  private final UUID id;
  private final String userId;
  private final Currency fromCurrency;
  private final BigDecimal originalAmount;
  private final Currency toCurrency;
  private final BigDecimal convertedAmount;
  private final BigDecimal rate;
  private final ZonedDateTime dateTime;
}

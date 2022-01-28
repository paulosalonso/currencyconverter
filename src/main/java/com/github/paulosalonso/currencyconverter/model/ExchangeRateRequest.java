package com.github.paulosalonso.currencyconverter.model;

import java.math.BigDecimal;
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
public class ExchangeRateRequest {
  private String userId;
  private Currency fromCurrency;
  private BigDecimal amount;
  private Currency toCurrency;
}

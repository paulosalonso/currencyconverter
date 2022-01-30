package com.github.paulosalonso.currencyconverter.api.dto;

import java.math.BigDecimal;
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
public class TransactionDto {
  private String id;
  private String userId;
  private String fromCurrency;
  private BigDecimal originalAmount;
  private String toCurrency;
  private BigDecimal convertedAmount;
  private BigDecimal rate;
  private ZonedDateTime dateTime;
}

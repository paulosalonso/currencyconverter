package com.github.paulosalonso.currencyconverter.api.dto;

import java.math.BigDecimal;
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
public class ExchangeRequestDto {
  private String userId;
  private String fromCurrency;
  private BigDecimal amount;
  private String toCurrency;
}

package com.github.paulosalonso.currencyconverter.api.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
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

  @NotBlank
  private String userId;

  @NotBlank
  @Size(min = 3, max = 3)
  private String fromCurrency;

  @Positive
  private BigDecimal amount;

  @NotBlank
  @Size(min = 3, max = 3)
  private String toCurrency;
}

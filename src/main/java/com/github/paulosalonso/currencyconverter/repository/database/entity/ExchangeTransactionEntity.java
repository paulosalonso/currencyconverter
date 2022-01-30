package com.github.paulosalonso.currencyconverter.repository.database.entity;

import com.github.paulosalonso.currencyconverter.model.Currency;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@Table("exchange_transaction")
public class ExchangeTransactionEntity {

  @Id
  private String id;
  private String userId;
  private Currency fromCurrency;
  private BigDecimal originalAmount;
  private Currency toCurrency;
  private BigDecimal convertedAmount;
  private BigDecimal rate;
  private Instant timestamp;
}

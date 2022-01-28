package com.github.paulosalonso.currencyconverter.model;

import static com.github.paulosalonso.currencyconverter.model.Currency.BRL;
import static com.github.paulosalonso.currencyconverter.model.Currency.EUR;
import static com.github.paulosalonso.currencyconverter.model.Currency.JPY;
import static com.github.paulosalonso.currencyconverter.model.Currency.USD;
import static java.lang.String.format;

import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ExchangeRequest {
  private final String userId;
  private final Currency fromCurrency;
  private final BigDecimal amount;
  private final Currency toCurrency;

  private ExchangeRequest(final String userId,
      final Currency fromCurrency, final BigDecimal amount, final Currency toCurrency) {

    this.userId = userId;
    this.fromCurrency = fromCurrency;
    this.amount = amount;
    this.toCurrency = toCurrency;
  }

  public static ExchangeRequest of(final String userId,
      final String fromCurrency, final BigDecimal amount, final String toCurrent) {

    final var fromCurrencyEnum = mapCurrency(fromCurrency, "fromCurrency", EUR.name());
    final var toCurrencyEnum = mapCurrency(toCurrent,
        "toCurrency", BRL.name(), EUR.name(), JPY.name(), USD.name());

    return new ExchangeRequest(userId, fromCurrencyEnum, amount, toCurrencyEnum);
  }

  private static Currency mapCurrency(String value, String field, String... allowedValues) {
    try {
      checkIfIsAllowedValue(value, allowedValues);

      return Currency.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(format(
          "Field %s has a invalid value. Allowed values: %s",
          field,
          String.join(", ", allowedValues)));
    }
  }

  private static void checkIfIsAllowedValue(String value, String... allowedValues) {
    boolean allowed = false;

    for (String allowedValue : allowedValues) {
      if (value.equals(allowedValue)) {
        allowed = true;
        break;
      }
    }

    if (!allowed) {
      throw new IllegalArgumentException();
    }
  }
}

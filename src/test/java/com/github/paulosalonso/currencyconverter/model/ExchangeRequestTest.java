package com.github.paulosalonso.currencyconverter.model;

import static com.github.paulosalonso.currencyconverter.model.Currency.BRL;
import static com.github.paulosalonso.currencyconverter.model.Currency.EUR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ExchangeRequestTest {

  @Test
  void givenValidParametersWhenConstructThenReturnExchangeRateRequestInstance() {
    final var userId = "user-id";
    final var fromCurrency = "EUR";
    final var amount = BigDecimal.TEN;
    final var toCurrency = "BRL";

    final var exchangeRateRequest =
        ExchangeRequest.of(userId, fromCurrency, amount, toCurrency);

    assertThat(exchangeRateRequest.getUserId()).isEqualTo(userId);
    assertThat(exchangeRateRequest.getFromCurrency()).isEqualTo(EUR);
    assertThat(exchangeRateRequest.getAmount()).isEqualTo(amount);
    assertThat(exchangeRateRequest.getToCurrency()).isEqualTo(BRL);
  }

  @Test
  void givenNonexistentFromCurrencyWhenConstructThenThrowsIllegalArgumentException() {
    final var userId = "user-id";
    final var fromCurrency = "INVALID";
    final var amount = BigDecimal.TEN;
    final var toCurrency = "BRL";

    assertThatThrownBy(() -> ExchangeRequest.of(userId, fromCurrency, amount, toCurrency))
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessage("Field fromCurrency has a invalid value. Allowed values: EUR");
  }

  @Test
  void givenNotAllowedFromCurrencyWhenConstructThenThrowsIllegalArgumentException() {
    final var userId = "user-id";
    final var fromCurrency = "USD";
    final var amount = BigDecimal.TEN;
    final var toCurrency = "BRL";

    assertThatThrownBy(() -> ExchangeRequest.of(userId, fromCurrency, amount, toCurrency))
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessage("Field fromCurrency has a invalid value. Allowed values: EUR");
  }

  @Test
  void givenNonexistentToCurrencyWhenConstructThenThrowsIllegalArgumentException() {
    final var userId = "user-id";
    final var fromCurrency = "EUR";
    final var amount = BigDecimal.TEN;
    final var toCurrency = "INVALID";

    assertThatThrownBy(() -> ExchangeRequest.of(userId, fromCurrency, amount, toCurrency))
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessage("Field toCurrency has a invalid value. Allowed values: BRL, EUR, JPY, USD");
  }

}

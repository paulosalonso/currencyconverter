package com.github.paulosalonso.currencyconverter.api;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.paulosalonso.currencyconverter.BaseIT;
import com.github.paulosalonso.currencyconverter.api.dto.ExchangeRequestDto;
import com.github.paulosalonso.currencyconverter.repository.http.dto.ExchangeRateResponseDto;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;


public class ExchangeEndpointIT extends BaseIT {

  @Test
  void givenParametersWhenRequestCurrencyExchangeThenReturnTransactionWithStatusCode200()
      throws JsonProcessingException {

    final var now = ZonedDateTime.now(UTC);
    final var userId = "user-id";
    final var fromCurrency = "EUR";
    final var toCurrency = "BRL";
    final var exchangeRateApiResponse = ExchangeRateResponseDto.builder()
        .success(true)
        .base("EUR")
        .rates(Map.of("BRL", 6.01))
        .timestamp(now.toEpochSecond())
        .date(now.toLocalDate())
        .build();

    mockExchangeRateApi(userId, fromCurrency, toCurrency, exchangeRateApiResponse);

    given()
        .accept(JSON)
        .contentType(JSON)
        .body(ExchangeRequestDto.builder()
            .userId("user-id")
            .fromCurrency("EUR")
            .amount(BigDecimal.TEN)
            .toCurrency("BRL")
            .build())
        .when()
        .post("/v1/exchanges")
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("userId", equalTo("user-id"))
        .body("fromCurrency", equalTo("EUR"))
        .body("originalAmount", equalTo(10))
        .body("toCurrency", equalTo("BRL"))
        .body("convertedAmount", equalTo(60.1F))
        .body("rate", equalTo(6.01F))
        .body("dateTime", equalTo(DATE_FORMATTER.format(now)));

    verifyExchangeRateApi(userId, fromCurrency, toCurrency);
  }
}

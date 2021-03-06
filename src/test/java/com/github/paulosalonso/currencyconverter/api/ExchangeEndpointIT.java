package com.github.paulosalonso.currencyconverter.api;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
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

  @Test
  void givenAnInvalidParameterWhenRequestCurrencyExchangeThenReturnErrorWithStatusCode400() {
    given()
        .accept(JSON)
        .contentType(JSON)
        .body(ExchangeRequestDto.builder()
            .fromCurrency("EUR")
            .amount(BigDecimal.TEN)
            .toCurrency("BRL")
            .build())
        .when()
        .post("/v1/exchanges")
        .then()
        .statusCode(400)
        .body("status", equalTo(400))
        .body("message", equalTo("Invalid data. Violations: property 'userId' must not be blank"))
        .body("timestamp", notNullValue());
  }

  @Test
  void givenAnUserIdWhenGetAllTransactionsThenReturnArrayOfTransactionsWithStatus200() {
    given()
        .accept(JSON)
        .contentType(JSON)
        .queryParam("userId", "1234567890")
        .queryParam("pageSize", "50")
        .when()
        .get("/v1/exchanges")
        .then()
        .statusCode(200)
        .body("$", hasSize(3))
        .body("[0].id", equalTo("c6ef37ae-e272-4fbf-be2d-3ab2aaf7d960"))
        .body("[1].id", equalTo("8ee80765-7bb8-44ea-8f30-8cd10f1c9f3c"))
        .body("[2].id", equalTo("f71f4d9a-af52-4a6a-b99c-973defbbf670"));
  }

  @Test
  void givenPageAndPageSizeWhenGetAllTransactionsThenReturnRequestedPageWithStatus200() {
    given()
        .accept(JSON)
        .contentType(JSON)
        .queryParam("userId", "1234567890")
        .queryParam("page", "1")
        .queryParam("pageSize", "1")
        .when()
        .get("/v1/exchanges")
        .then()
        .statusCode(200)
        .body("$", hasSize(1))
        .body("[0].id", equalTo("8ee80765-7bb8-44ea-8f30-8cd10f1c9f3c"))
        .body("[0].userId", equalTo("1234567890"))
        .body("[0].fromCurrency", equalTo("EUR"))
        .body("[0].originalAmount", equalTo(10))
        .body("[0].toCurrency", equalTo("USD"))
        .body("[0].convertedAmount", equalTo(11.2F))
        .body("[0].rate", equalTo(1.12F))
        .body("[0].dateTime", equalTo("2022-01-30T19:30:00Z"));
  }

  @Test
  void givenOnlyPageWhenGetAllTransactionsThenReturnPageWithDefaultSizeAndStatus200() {
    given()
        .accept(JSON)
        .contentType(JSON)
        .queryParam("userId", "1234567890")
        .queryParam("page", "0")
        .when()
        .get("/v1/exchanges")
        .then()
        .statusCode(200)
        .body("$", hasSize(DEFAULT_PAGE_SIZE))
        .body("[0].id", equalTo("c6ef37ae-e272-4fbf-be2d-3ab2aaf7d960"))
        .body("[1].id", equalTo("8ee80765-7bb8-44ea-8f30-8cd10f1c9f3c"));
  }

  @Test
  void givenOnlyPageSizeWhenGetAllTransactionsThenReturnPageZeroWithRequestdSizeAndStatus200() {
    given()
        .accept(JSON)
        .contentType(JSON)
        .queryParam("userId", "1234567890")
        .queryParam("pageSize", "1")
        .when()
        .get("/v1/exchanges")
        .then()
        .statusCode(200)
        .body("$", hasSize(1))
        .body("[0].id", equalTo("c6ef37ae-e272-4fbf-be2d-3ab2aaf7d960"));
  }

  @Test
  void givenNoUserIdParameterWhenGetAllTransactionsThenReturnStatusCode400() {
    given()
        .accept(JSON)
        .contentType(JSON)
        .when()
        .get("/v1/exchanges")
        .then()
        .statusCode(400)
        .body("status", equalTo(400))
        .body("message", equalTo("The param 'userId' is required"))
        .body("timestamp", notNullValue());
  }

  @Test
  void givenNonNumericPageParameterWhenRequestCurrencyExchangeThenReturnErrorWithStatusCode400() {
    given()
        .accept(JSON)
        .contentType(JSON)
        .queryParam("userId", "1234567890")
        .queryParam("page", "page")
        .when()
        .get("/v1/exchanges")
        .then()
        .statusCode(400)
        .body("status", equalTo(400))
        .body("message", equalTo("The value of property 'page' is invalid. It should be an integer value."))
        .body("timestamp", notNullValue());
  }

  @Test
  void givenNonNumericPageSizeParameterWhenRequestCurrencyExchangeThenReturnErrorWithStatusCode400() {
    given()
        .accept(JSON)
        .contentType(JSON)
        .queryParam("userId", "1234567890")
        .queryParam("pageSize", "pageSize")
        .when()
        .get("/v1/exchanges")
        .then()
        .statusCode(400)
        .body("status", equalTo(400))
        .body("message", equalTo("The value of property 'pageSize' is invalid. It should be an integer value."))
        .body("timestamp", notNullValue());
  }
}

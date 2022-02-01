package com.github.paulosalonso.currencyconverter;

import static com.github.paulosalonso.currencyconverter.BaseIT.DEFAULT_PAGE_SIZE;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.mockserver.springtest.MockServerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = "exchange.get.defaultPageSize=" + DEFAULT_PAGE_SIZE)
@MockServerTest("spring.feign.exchange-rate-api-base-url=http://localhost:${mockServerPort}")
public class BaseIT {

  @LocalServerPort
  protected int port;

  protected MockServerClient mockServerClient;

  private final ObjectMapper MAPPER;

  protected final DateTimeFormatter DATE_FORMATTER;

  protected static final int DEFAULT_PAGE_SIZE = 2;

  public BaseIT() {
    MAPPER = new ObjectMapper();
    MAPPER.registerModule(new JavaTimeModule());

    DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");
  }

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
  }

  protected <T> void mockExchangeRateApi(String userId,
      String fromCurrency, String toCurrency, T response) throws JsonProcessingException {

    mockServerClient.when(
            request()
                .withMethod("GET")
                .withPath("/v1/latest")
                .withQueryStringParameter("access_key", userId)
                .withQueryStringParameter("base", fromCurrency)
                .withQueryStringParameter("symbols", toCurrency)
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withHeader(ACCEPT, APPLICATION_JSON_VALUE))
        .respond(
            response()
                .withBody(MAPPER.writeValueAsString(response))
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        );
  }

  protected void verifyExchangeRateApi(
      String userId, String fromCurrency, String toCurrency) {

    mockServerClient.verify(
        request()
            .withMethod("GET")
            .withPath("/v1/latest")
            .withQueryStringParameter("access_key", userId)
            .withQueryStringParameter("base", fromCurrency)
            .withQueryStringParameter("symbols", toCurrency)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withHeader(ACCEPT, APPLICATION_JSON_VALUE));

  }

}

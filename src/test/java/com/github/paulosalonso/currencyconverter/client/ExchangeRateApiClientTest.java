package com.github.paulosalonso.currencyconverter.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.github.paulosalonso.currencyconverter.client.dto.ExchangeRateErrorDto;
import com.github.paulosalonso.currencyconverter.client.dto.ExchangeRateErrorDto.Error;
import com.github.paulosalonso.currencyconverter.client.dto.ExchangeRateResponseDto;
import java.net.URI;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ExchangeRateApiClientTest {

  private static final String PATH = "/v1/latest";

  @InjectMocks
  private ExchangeRateApiClient exchangeRateApiClient;

  @Mock
  private WebClient webClient;

  @Mock
  private UriBuilder uriBuilder;

  @Mock
  private URI uri;

  @Mock
  private RequestBodyUriSpec requestBodyUriSpec;

  @Mock
  private RequestBodySpec requestBodySpec;

  @Mock
  private ResponseSpec responseSpec;

  @Mock
  private ClientResponse clientResponse;

  @Mock
  private Mono<ExchangeRateResponseDto> response;

  @Test
  void givenUserIdAndCurrenciesWhenGetCurrentExchangeRateThenReturnMono() {
    final var userId = "user-id";
    final var fromCurrency = "from-currency";
    final var toCurrency = "to-currency";

    when(webClient.method(GET)).thenReturn(requestBodyUriSpec);
    when(uriBuilder.path(PATH)).thenReturn(uriBuilder);
    when(uriBuilder.queryParam(anyString(), anyString())).thenReturn(uriBuilder);
    when(uriBuilder.build()).thenReturn(uri);
    when(requestBodyUriSpec.uri(any(Function.class))).thenAnswer(invocationOnMock -> {
      final var uriFunction = invocationOnMock.getArgument(0, Function.class);
      assertThat(uriFunction.apply(uriBuilder)).isSameAs(uri);
      return requestBodySpec;
    });
    when(requestBodySpec.accept(APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ExchangeRateResponseDto.class)).thenReturn(response);

    final var result = exchangeRateApiClient.getCurrentExchangeRate(userId, fromCurrency, toCurrency);

    assertThat(result).isEqualTo(response);

    verify(webClient).method(GET);
    verify(uriBuilder).path(PATH);
    verify(uriBuilder).queryParam("access_key", userId);
    verify(uriBuilder).queryParam("base", fromCurrency);
    verify(uriBuilder).queryParam("symbols", toCurrency);
    verify(uriBuilder).build();
    verify(requestBodyUriSpec).uri(any(Function.class));
    verify(requestBodySpec).accept(APPLICATION_JSON);
    verify(requestBodySpec).contentType(APPLICATION_JSON);
    verify(requestBodySpec).retrieve();
    verify(responseSpec, times(2)).onStatus(any(Predicate.class), any(Function.class));
    verify(responseSpec).bodyToMono(ExchangeRateResponseDto.class);
  }

  @Test
  void givenUserIdAndCurrenciesWhenClientReturn4xxErrorThenReturnErrorMono() {
    final var userId = "user-id";
    final var fromCurrency = "from-currency";
    final var toCurrency = "to-currency";
    final var exchangeRateErrorDto = ExchangeRateErrorDto.builder()
        .error(Error.builder()
            .code("code")
            .message("message")
            .build())
        .build();
    final var exchangeRateErrorDtoMono = Mono.just(exchangeRateErrorDto);

    when(webClient.method(GET)).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.accept(APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(clientResponse.body(any(BodyExtractor.class))).thenReturn(exchangeRateErrorDtoMono);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class)))
        .thenAnswer(invocationOnMock -> {
          final var predicate = invocationOnMock.getArgument(0, Predicate.class);
          final var function = invocationOnMock.getArgument(1, Function.class);

          if (predicate.test(HttpStatus.BAD_REQUEST)) {
            final var errorMono = (Mono) function.apply(clientResponse);
            when(responseSpec.bodyToMono(ExchangeRateResponseDto.class)).thenReturn(errorMono);
          }

          return responseSpec;
        });

    final var result = exchangeRateApiClient.getCurrentExchangeRate(userId, fromCurrency, toCurrency);

    StepVerifier.create(result)
        .expectErrorSatisfies(throwable -> {
          assertThat(throwable).isExactlyInstanceOf(IllegalArgumentException.class);
          assertThat(throwable.getMessage()).isEqualTo(exchangeRateErrorDto.getError().getCode());
        })
        .verify();

    verify(webClient).method(GET);
    verify(requestBodyUriSpec).uri(any(Function.class));
    verify(requestBodySpec).accept(APPLICATION_JSON);
    verify(requestBodySpec).contentType(APPLICATION_JSON);
    verify(requestBodySpec).retrieve();
    verify(responseSpec, times(2)).onStatus(any(Predicate.class), any(Function.class));
    verify(responseSpec).bodyToMono(ExchangeRateResponseDto.class);
    verify(clientResponse).body(any(BodyExtractor.class));
  }

  @Test
  void givenUserIdAndCurrenciesWhenClientReturn5xxThenReturnErrorMono() {
    final var userId = "user-id";
    final var fromCurrency = "from-currency";
    final var toCurrency = "to-currency";
    final var exchangeRateErrorDto = ExchangeRateErrorDto.builder()
        .error(Error.builder()
            .code("code")
            .message("message")
            .build())
        .build();
    final var exchangeRateErrorDtoMono = Mono.just(exchangeRateErrorDto);

    when(webClient.method(GET)).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.accept(APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(clientResponse.body(any(BodyExtractor.class))).thenReturn(exchangeRateErrorDtoMono);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class)))
        .thenAnswer(invocationOnMock -> {
          final var predicate = invocationOnMock.getArgument(0, Predicate.class);
          final var function = invocationOnMock.getArgument(1, Function.class);

          if (predicate.test(HttpStatus.INTERNAL_SERVER_ERROR)) {
            final var errorMono = (Mono) function.apply(clientResponse);
            when(responseSpec.bodyToMono(ExchangeRateResponseDto.class)).thenReturn(errorMono);
          }

          return responseSpec;
        });

    final var result = exchangeRateApiClient.getCurrentExchangeRate(userId, fromCurrency, toCurrency);

    StepVerifier.create(result)
        .expectErrorSatisfies(throwable -> {
          assertThat(throwable).isExactlyInstanceOf(RuntimeException.class);
          assertThat(throwable.getMessage()).isEqualTo(exchangeRateErrorDto.getError().getCode());
        })
        .verify();

    verify(webClient).method(GET);
    verify(requestBodyUriSpec).uri(any(Function.class));
    verify(requestBodySpec).accept(APPLICATION_JSON);
    verify(requestBodySpec).contentType(APPLICATION_JSON);
    verify(requestBodySpec).retrieve();
    verify(responseSpec, times(2)).onStatus(any(Predicate.class), any(Function.class));
    verify(responseSpec).bodyToMono(ExchangeRateResponseDto.class);
    verify(clientResponse).body(any(BodyExtractor.class));
  }
}

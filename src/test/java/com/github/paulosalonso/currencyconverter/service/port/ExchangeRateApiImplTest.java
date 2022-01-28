package com.github.paulosalonso.currencyconverter.service.port;

import static com.github.paulosalonso.currencyconverter.model.Currency.BRL;
import static com.github.paulosalonso.currencyconverter.model.Currency.EUR;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.github.paulosalonso.currencyconverter.client.ExchangeRateApiClient;
import com.github.paulosalonso.currencyconverter.client.dto.ExchangeRateResponseDto;
import com.github.paulosalonso.currencyconverter.model.Currency;
import com.github.paulosalonso.currencyconverter.model.ExchangeRateRequest;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ExchangeRateApiImplTest {

  @InjectMocks
  private ExchangeRateApiPort exchangeRateApiPort;

  @Mock
  private ExchangeRateApiClient exchangeRateApiClient;

  @Test
  void givenValidParametersWhenGetCurrentExchangeRateThenReturnExchangeRateInstance() {
    final var userId = "user-id";
    final var fromCurrency = EUR;
    final var toCurrency = BRL;

    final var dto = ExchangeRateResponseDto.builder()
        .success(true)
        .timestamp(ZonedDateTime.now(UTC).toEpochSecond())
        .date(LocalDate.now())
        .base(fromCurrency.name())
        .rates(Map.of(toCurrency.name(), 5.0))
        .build();
    final var monoDto = Mono.just(dto);

    when(exchangeRateApiClient.getCurrentExchangeRate(userId, fromCurrency.name(), toCurrency.name()))
        .thenReturn(monoDto);

    final var request = ExchangeRateRequest.builder()
        .userId(userId)
        .fromCurrency(fromCurrency)
        .toCurrency(toCurrency)
        .build();

    final var result = exchangeRateApiPort.getCurrentExchangeRate(request);

    StepVerifier.create(result)
        .assertNext(exchangeRate -> {
          assertThat(exchangeRate.getDateTime().toEpochSecond()).isEqualTo(dto.getTimestamp());
          assertThat(exchangeRate.getFromCurrency()).isEqualTo(Currency.valueOf(fromCurrency.name()));
          assertThat(exchangeRate.getToCurrency()).isEqualTo(Currency.valueOf(toCurrency.name()));
          assertThat(exchangeRate.getRate()).isEqualTo(dto.getRates().get(toCurrency.name()));
        })
        .expectComplete()
        .verify();

    verify(exchangeRateApiClient).getCurrentExchangeRate(userId, fromCurrency.name(), toCurrency.name());
    verifyNoMoreInteractions(exchangeRateApiClient);
  }

  @ParameterizedTest
  @MethodSource("getClientExceptions")
  void givenParametersWhenOccursAnErrorGettingExchangeRateThenReturnThrowableMono(
      Throwable clientError, Class<Throwable> expectedError, String expectedErrorMessage) {

    final var userId = "user-id";
    final var fromCurrency = EUR;
    final var toCurrency = BRL;

    final Mono<ExchangeRateResponseDto> errorMono = Mono.error(clientError);

    when(exchangeRateApiClient.getCurrentExchangeRate(userId, fromCurrency.name(), toCurrency.name()))
        .thenReturn(errorMono);

    final var request = ExchangeRateRequest.builder()
        .userId(userId)
        .fromCurrency(fromCurrency)
        .toCurrency(toCurrency)
        .build();

    final var result = exchangeRateApiPort.getCurrentExchangeRate(request);

    StepVerifier.create(result)
        .expectErrorSatisfies(throwable -> {
          assertThat(throwable).isExactlyInstanceOf(expectedError);
          assertThat(throwable.getMessage()).isEqualTo(expectedErrorMessage);
        })
        .verify();

    verify(exchangeRateApiClient).getCurrentExchangeRate(userId, fromCurrency.name(), toCurrency.name());
  }

  private static Stream<Arguments> getClientExceptions() {
    return Stream.of(
        arguments(new IllegalArgumentException("invalid_access_key"), IllegalArgumentException.class, "Invalid user id"),
        arguments(new IllegalArgumentException("invalid_base_currency"), IllegalArgumentException.class, "Invalid base currency"),
        arguments(new IllegalArgumentException("base_currency_access_restricted"), IllegalArgumentException.class, "Base currency is not available"),
        arguments(new IllegalArgumentException("invalid_currency_codes"), IllegalArgumentException.class, "Invalid target currency"),
        arguments(new RuntimeException("unknown_error"), RuntimeException.class, "Unknown error"));
  }

}

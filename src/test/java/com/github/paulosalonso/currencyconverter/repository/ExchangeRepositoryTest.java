package com.github.paulosalonso.currencyconverter.repository;

import static com.github.paulosalonso.currencyconverter.model.Currency.BRL;
import static com.github.paulosalonso.currencyconverter.model.Currency.EUR;
import static com.github.paulosalonso.currencyconverter.repository.mapper.ExchangeRateResponseDtoMapper.toModel;
import static com.github.paulosalonso.currencyconverter.repository.mapper.ExchangeTransactionEntityMapper.toModel;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.github.paulosalonso.currencyconverter.model.Currency;
import com.github.paulosalonso.currencyconverter.model.ExchangeRequest;
import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import com.github.paulosalonso.currencyconverter.repository.database.ExchangeTransactionEntityRepository;
import com.github.paulosalonso.currencyconverter.repository.http.ExchangeRateApiClient;
import com.github.paulosalonso.currencyconverter.repository.http.dto.ExchangeRateResponseDto;
import com.github.paulosalonso.currencyconverter.repository.mapper.ExchangeTransactionEntityMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
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
class ExchangeRepositoryTest {

  @InjectMocks
  private ExchangeRepository exchangeRepository;

  @Mock
  private ExchangeRateApiClient exchangeRateApiClient;

  @Mock
  private ExchangeTransactionEntityRepository exchangeTransactionEntityRepository;

  @Test
  void givenValidParametersWhenGetCurrentExchangeRateThenReturnExchangeRateInstance() {
    final var userId = "user-id";
    final var fromCurrency = "EUR";
    final var toCurrency = "BRL";

    final var dto = ExchangeRateResponseDto.builder()
        .success(true)
        .timestamp(ZonedDateTime.now(UTC).toEpochSecond())
        .date(LocalDate.now())
        .base(fromCurrency)
        .rates(Map.of(toCurrency, 5.0))
        .build();
    final var monoDto = Mono.just(dto);

    when(exchangeRateApiClient.getCurrentExchangeRate(userId, fromCurrency, toCurrency))
        .thenReturn(monoDto);

    final var request = ExchangeRequest.of(userId, fromCurrency, BigDecimal.ZERO, toCurrency);
    final var result = exchangeRepository.getCurrentExchangeRate(request);

    StepVerifier.create(result)
        .assertNext(exchangeRate ->
            assertThat(exchangeRate).isEqualTo(toModel(Currency.valueOf(toCurrency), dto)))
        .expectComplete()
        .verify();

    verify(exchangeRateApiClient).getCurrentExchangeRate(userId, fromCurrency, toCurrency);
    verifyNoMoreInteractions(exchangeRateApiClient);
  }

  @ParameterizedTest
  @MethodSource("getClientExceptions")
  void givenParametersWhenOccursAnErrorGettingExchangeRateThenReturnThrowableMono(
      Throwable clientError, Class<Throwable> expectedError, String expectedErrorMessage) {

    final var userId = "user-id";
    final var fromCurrency = "EUR";
    final var toCurrency = "BRL";

    final Mono<ExchangeRateResponseDto> errorMono = Mono.error(clientError);

    when(exchangeRateApiClient.getCurrentExchangeRate(userId, fromCurrency, toCurrency))
        .thenReturn(errorMono);

    final var request = ExchangeRequest.of(userId, fromCurrency, BigDecimal.ZERO, toCurrency);

    final var result = exchangeRepository.getCurrentExchangeRate(request);

    StepVerifier.create(result)
        .expectErrorSatisfies(throwable -> {
          assertThat(throwable).isExactlyInstanceOf(expectedError);
          assertThat(throwable.getMessage()).isEqualTo(expectedErrorMessage);
        })
        .verify();

    verify(exchangeRateApiClient).getCurrentExchangeRate(userId, fromCurrency, toCurrency);
  }

  @Test
  void givenAnExchangeTransactionWhenSaveThenCallDatabaseRepository() {
    final var exchangeTransaction = ExchangeTransaction.builder()
        .id(UUID.randomUUID())
        .userId("user-id")
        .fromCurrency(EUR)
        .originalAmount(BigDecimal.ZERO)
        .toCurrency(BRL)
        .convertedAmount(BigDecimal.ONE)
        .rate(BigDecimal.TEN)
        .dateTime(ZonedDateTime.now(UTC))
        .build();

    final var entity = ExchangeTransactionEntityMapper.toEntity(exchangeTransaction);
    final var savedEntity = entity.toBuilder()
        .id(UUID.randomUUID().toString())
        .build();

    when(exchangeTransactionEntityRepository.save(entity)).thenReturn(Mono.just(savedEntity));

    final var result = exchangeRepository.save(exchangeTransaction);

    StepVerifier.create(result)
        .assertNext(saved -> assertThat(saved).isEqualTo(toModel(savedEntity)))
        .expectComplete()
        .verify();

    verify(exchangeTransactionEntityRepository).save(entity);
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
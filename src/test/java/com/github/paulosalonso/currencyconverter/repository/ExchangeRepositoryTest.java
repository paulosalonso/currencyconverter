package com.github.paulosalonso.currencyconverter.repository;

import static com.github.paulosalonso.currencyconverter.repository.mapper.ExchangeRateResponseDtoMapper.toModel;
import static com.github.paulosalonso.currencyconverter.repository.mapper.ExchangeTransactionEntityMapper.toModel;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.github.paulosalonso.currencyconverter.model.ExchangeRequest;
import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import com.github.paulosalonso.currencyconverter.repository.database.ExchangeTransactionEntityRepository;
import com.github.paulosalonso.currencyconverter.repository.database.entity.ExchangeTransactionEntity;
import com.github.paulosalonso.currencyconverter.repository.http.ExchangeRateApiClient;
import com.github.paulosalonso.currencyconverter.repository.http.dto.ExchangeRateResponseDto;
import com.github.paulosalonso.currencyconverter.repository.mapper.ExchangeTransactionEntityMapper;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
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

    final var request = ExchangeRequest.builder()
        .userId(userId)
        .fromCurrency(fromCurrency)
        .amount(BigDecimal.ZERO)
        .toCurrency(toCurrency)
        .build();
    final var result = exchangeRepository.getCurrentExchangeRate(request);

    StepVerifier.create(result)
        .assertNext(exchangeRate ->
            assertThat(exchangeRate).isEqualTo(toModel(toCurrency, dto)))
        .expectComplete()
        .verify();

    verify(exchangeRateApiClient).getCurrentExchangeRate(userId, fromCurrency, toCurrency);
    verifyNoMoreInteractions(exchangeRateApiClient);
    verifyNoInteractions(exchangeTransactionEntityRepository);
  }

  @Test
  void givenParametersWhenOccursAnErrorGettingExchangeRateThenReturnThrowableMono() {
    final var userId = "user-id";
    final var fromCurrency = "EUR";
    final var toCurrency = "BRL";
    final var exception = new RuntimeException();
    final Mono<ExchangeRateResponseDto> errorMono = Mono.error(exception);

    when(exchangeRateApiClient.getCurrentExchangeRate(userId, fromCurrency, toCurrency))
        .thenReturn(errorMono);

    final var request = ExchangeRequest.builder()
        .userId(userId)
        .fromCurrency(fromCurrency)
        .amount(BigDecimal.ZERO)
        .toCurrency(toCurrency)
        .build();

    final var result = exchangeRepository.getCurrentExchangeRate(request);

    StepVerifier.create(result)
        .expectErrorSatisfies(resultException -> assertThat(resultException).isSameAs(exception))
        .verify();

    verify(exchangeRateApiClient).getCurrentExchangeRate(userId, fromCurrency, toCurrency);
    verifyNoMoreInteractions(exchangeRateApiClient);
    verifyNoInteractions(exchangeTransactionEntityRepository);
  }

  @Test
  void givenAnExchangeTransactionWhenSaveThenCallDatabaseRepository() {
    final var exchangeTransaction = ExchangeTransaction.builder()
        .id(UUID.randomUUID())
        .userId("user-id")
        .fromCurrency("EUR")
        .originalAmount(BigDecimal.ZERO)
        .toCurrency("BRL")
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
    verifyNoMoreInteractions(exchangeTransactionEntityRepository);
    verifyNoInteractions(exchangeRateApiClient);
  }

  @Test
  void givenAnUserIdWhenFindAllByUserIdThenReturnExchangeTransactionFlux() {
    final var userId = "user-id";
    final var entity = ExchangeTransactionEntity.builder()
        .id(UUID.randomUUID().toString())
        .userId("user-id")
        .fromCurrency("EUR")
        .originalAmount(BigDecimal.ZERO)
        .toCurrency("BRL")
        .convertedAmount(BigDecimal.ONE)
        .rate(BigDecimal.TEN)
        .timestamp(Instant.now(Clock.systemUTC()))
        .build();
    final var flux = Flux.just(entity);

    when(exchangeTransactionEntityRepository.findAllByUserId(userId)).thenReturn(flux);

    final var result = exchangeRepository.findAllTransactionsByUserId(userId);

    StepVerifier.create(result)
        .assertNext(exchangeTransaction ->
            assertThat(exchangeTransaction).isEqualTo(toModel(entity)))
        .expectComplete()
        .verify();

    verify(exchangeTransactionEntityRepository).findAllByUserId(userId);
    verifyNoMoreInteractions(exchangeTransactionEntityRepository);
    verifyNoInteractions(exchangeRateApiClient);
  }

}

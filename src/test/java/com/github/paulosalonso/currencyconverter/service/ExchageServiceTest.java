package com.github.paulosalonso.currencyconverter.service;

import static com.github.paulosalonso.currencyconverter.model.Currency.BRL;
import static com.github.paulosalonso.currencyconverter.model.Currency.EUR;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.github.paulosalonso.currencyconverter.model.ExchangeRate;
import com.github.paulosalonso.currencyconverter.model.ExchangeRequest;
import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import com.github.paulosalonso.currencyconverter.service.port.ExchangePort;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ExchageServiceTest {

  @InjectMocks
  private ExchageService exchageService;

  @Mock
  private ExchangePort exchangePort;

  @Captor
  private ArgumentCaptor<ExchangeTransaction> exchangeTransactionCaptor;

  @Test
  void givenARequestWhenConvertThenReturnAExchangeRateTransaction() {
    final var request = ExchangeRequest.of("user-id", "EUR", BigDecimal.TEN, "BRL");

    final var eurToBrlRate = BigDecimal.valueOf(6.1);

    final var exchangeRate = ExchangeRate.builder()
        .dateTime(ZonedDateTime.now(UTC))
        .fromCurrency(EUR)
        .toCurrency(BRL)
        .rate(eurToBrlRate)
        .build();

    final var exchangeRateMono = Mono.just(exchangeRate);
    final var savedExchangeTransaction = createExchangeTransaction();

    when(exchangePort.getCurrentExchangeRate(request)).thenReturn(exchangeRateMono);
    when(exchangePort.save(any(ExchangeTransaction.class))).thenReturn(Mono.just(savedExchangeTransaction));

    final var result = exchageService.exchange(request);

    StepVerifier.create(result)
        .assertNext(transaction -> assertThat(transaction).isSameAs(savedExchangeTransaction))
        .expectComplete()
        .verify();

    verify(exchangePort).getCurrentExchangeRate(request);
    verify(exchangePort).save(exchangeTransactionCaptor.capture());
    verifyNoMoreInteractions(exchangePort);

    final var transaction = exchangeTransactionCaptor.getValue();
    assertThat(transaction.getId()).isNull();
    assertThat(transaction.getUserId()).isEqualTo(request.getUserId());
    assertThat(transaction.getFromCurrency()).isEqualTo(request.getFromCurrency());
    assertThat(transaction.getOriginalAmount()).isEqualTo(request.getAmount());
    assertThat(transaction.getToCurrency()).isEqualTo(request.getToCurrency());
    assertThat(transaction.getConvertedAmount())
        .isEqualTo(request.getAmount().multiply(eurToBrlRate));
    assertThat(transaction.getDateTime()).isEqualTo(exchangeRate.getDateTime());
  }

  private ExchangeTransaction createExchangeTransaction() {
    return ExchangeTransaction.builder()
        .id(UUID.randomUUID())
        .userId("user-id")
        .fromCurrency(EUR)
        .originalAmount(BigDecimal.ZERO)
        .toCurrency(BRL)
        .convertedAmount(BigDecimal.ONE)
        .rate(BigDecimal.TEN)
        .dateTime(ZonedDateTime.now(UTC))
        .build();
  }
}

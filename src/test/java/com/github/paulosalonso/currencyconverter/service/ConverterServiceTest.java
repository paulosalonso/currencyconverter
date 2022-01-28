package com.github.paulosalonso.currencyconverter.service;

import static com.github.paulosalonso.currencyconverter.model.Currency.BRL;
import static com.github.paulosalonso.currencyconverter.model.Currency.EUR;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.github.paulosalonso.currencyconverter.model.ExchangeRate;
import com.github.paulosalonso.currencyconverter.model.ExchangeRateRequest;
import com.github.paulosalonso.currencyconverter.service.port.ExchangeRatePort;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ConverterServiceTest {

  @InjectMocks
  private ConverterService converterService;

  @Mock
  private ExchangeRatePort exchangeRatePort;

  @Test
  void givenARequestWhenConvertThenReturnAExchangeRateTransaction() {
    final var request = ExchangeRateRequest.builder()
        .userId("user-id")
        .fromCurrency(EUR)
        .toCurrency(BRL)
        .amount(BigDecimal.TEN)
        .build();

    final var eurToBrlRate = 6.1;

    final var exchangeRate = ExchangeRate.builder()
        .dateTime(ZonedDateTime.now(UTC))
        .fromCurrency(EUR)
        .toCurrency(BRL)
        .rate(eurToBrlRate)
        .build();

    final var exchangeRateMono = Mono.just(exchangeRate);

    when(exchangeRatePort.getCurrentExchangeRate(request)).thenReturn(exchangeRateMono);

    final var result = converterService.convert(request);

    StepVerifier.create(result)
        .assertNext(transaction -> {
          assertThat(transaction.getId()).isNotNull();
          assertThat(transaction.getUserId()).isEqualTo(request.getUserId());
          assertThat(transaction.getFromCurrency()).isEqualTo(request.getFromCurrency());
          assertThat(transaction.getOriginalAmount()).isEqualTo(request.getAmount());
          assertThat(transaction.getToCurrency()).isEqualTo(request.getToCurrency());
          assertThat(transaction.getConvertedAmount())
              .isEqualTo(request.getAmount().multiply(BigDecimal.valueOf(eurToBrlRate)));
          assertThat(transaction.getDateTime()).isEqualTo(exchangeRate.getDateTime());
        })
        .expectComplete()
        .verify();
  }
}

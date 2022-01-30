package com.github.paulosalonso.currencyconverter.service;

import static com.github.paulosalonso.currencyconverter.model.Currency.BRL;
import static com.github.paulosalonso.currencyconverter.model.Currency.EUR;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.github.paulosalonso.currencyconverter.model.ExchangeRate;
import com.github.paulosalonso.currencyconverter.model.ExchangeRequest;
import com.github.paulosalonso.currencyconverter.service.port.ExchangePort;
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
class ExchageServiceTest {

  @InjectMocks
  private ExchageService exchageService;

  @Mock
  private ExchangePort exchangePort;

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

    when(exchangePort.getCurrentExchangeRate(request)).thenReturn(exchangeRateMono);

    final var result = exchageService.convert(request);

    StepVerifier.create(result)
        .assertNext(transaction -> {
          assertThat(transaction.getId()).isNotNull();
          assertThat(transaction.getUserId()).isEqualTo(request.getUserId());
          assertThat(transaction.getFromCurrency()).isEqualTo(request.getFromCurrency());
          assertThat(transaction.getOriginalAmount()).isEqualTo(request.getAmount());
          assertThat(transaction.getToCurrency()).isEqualTo(request.getToCurrency());
          assertThat(transaction.getConvertedAmount())
              .isEqualTo(request.getAmount().multiply(eurToBrlRate));
          assertThat(transaction.getDateTime()).isEqualTo(exchangeRate.getDateTime());
        })
        .expectComplete()
        .verify();
  }
}

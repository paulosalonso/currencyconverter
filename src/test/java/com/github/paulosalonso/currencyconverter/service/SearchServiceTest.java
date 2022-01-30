package com.github.paulosalonso.currencyconverter.service;

import static com.github.paulosalonso.currencyconverter.model.Currency.BRL;
import static com.github.paulosalonso.currencyconverter.model.Currency.EUR;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.github.paulosalonso.currencyconverter.model.ExchangeTransaction;
import com.github.paulosalonso.currencyconverter.service.port.ExchangePort;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

  @InjectMocks
  private SearchService searchService;

  @Mock
  private ExchangePort exchangePort;

  @Test
  void givenAnUserIdWhenFindAllTransactionsByUserIdThenReturnExchageTransactionFlux() {
    final var userId = "user-id";
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
    final var flux = Flux.just(exchangeTransaction);

    when(exchangePort.findAllTransactionsByUserId(userId)).thenReturn(flux);

    final var result = searchService.findAllTransactionsByUserId(userId);

    StepVerifier.create(result)
        .assertNext(foundExchangeTransaction ->
            assertThat(exchangeTransaction).isEqualTo(foundExchangeTransaction))
        .expectComplete()
        .verify();

    verify(exchangePort).findAllTransactionsByUserId(userId);
    verifyNoMoreInteractions(exchangePort);
  }

}

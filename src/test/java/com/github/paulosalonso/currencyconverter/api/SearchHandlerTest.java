package com.github.paulosalonso.currencyconverter.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.github.paulosalonso.currencyconverter.SearchHandler;
import com.github.paulosalonso.currencyconverter.service.SearchService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SearchHandlerTest {

  private static final String USER_ID_PARAM = "userId";

  @InjectMocks
  private SearchHandler searchHandler;

  @Mock
  private SearchService searchService;

  @Mock
  private ServerRequest request;

  @Test
  void givenARequestWithoutRequiredParamWhenHandleItsThenReturnErrorMono() {
    when(request.queryParam(USER_ID_PARAM)).thenReturn(Optional.empty());

    final var response = searchHandler.handle(request);

    StepVerifier.create(response)
        .assertNext(serverResponse ->
            assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST))
        .expectComplete()
        .verify();
  }

}

package com.github.paulosalonso.currencyconverter.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class OpenApiConfigurationTest {

  private final OpenApiConfiguration openApiConfiguration = new OpenApiConfiguration();

  @Test
  void givenAppAndContactInformationsWhenCreateOpenAPIThenReturnAInstanceWithTheseInformations() {
    final var appVersion = "1.0.0";
    final var contactName = "contact-name";
    final var contactEmail = "contact-email";
    final var contactUrl = "contact-url";

    final var openApi = openApiConfiguration.openApi(
        appVersion, contactName, contactEmail, contactUrl);

    assertThat(openApi.getInfo().getTitle()).isEqualTo("Currency Converter API");
    assertThat(openApi.getInfo().getDescription()).isEqualTo("Perform currency convertions");
    assertThat(openApi.getInfo().getVersion()).isEqualTo(appVersion);
    assertThat(openApi.getInfo().getContact().getName()).isEqualTo(contactName);
    assertThat(openApi.getInfo().getContact().getEmail()).isEqualTo(contactEmail);
    assertThat(openApi.getInfo().getContact().getUrl()).isEqualTo(contactUrl);
  }
}

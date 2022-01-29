package com.github.paulosalonso.currencyconverter.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FlywayConfigurationTest {

  private FlywayConfiguration flywayConfiguration = new FlywayConfiguration();

  @Test
  void givenDatabaseConnecetionPropertiesWhenConfigureFlywayThenApplyTheseProperties() {
    final var url = "jdbc:h2:mem:";
    final var user = "user";
    final var password = "password";

    final var flyway = flywayConfiguration.flyway(url, user, password);

    assertThat(flyway).isNotNull();
    assertThat(flyway.getConfiguration()).isNotNull()
        .satisfies(configuration -> {
          assertThat(configuration.getUrl()).isEqualTo(url);
          assertThat(configuration.getUser()).isEqualTo(user);
          assertThat(configuration.getPassword()).isEqualTo(password);
        });
  }

}

package com.github.paulosalonso.currencyconverter.configuration;

import static org.flywaydb.core.Flyway.configure;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration {

  @Bean(initMethod = "migrate")
  public Flyway flyway(
      @Value("${spring.flyway.url}") final String url,
      @Value("${spring.flyway.user}") final String user,
      @Value("${spring.flyway.password}") final String password) {

    var flyway = new Flyway(configure()
        .baselineOnMigrate(false)
        .dataSource(url, user, password));

    return flyway;
  }
}
package com.github.paulosalonso.currencyconverter.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

  @Bean
  public OpenAPI openApi(
      @Value("${build.version}") String appVersion,
      @Value("${openApi.contact.name}") String contactName,
      @Value("${openApi.contact.email}") String contactEmail,
      @Value("${openApi.contact.url}") String contactUrl) {

    return new OpenAPI()
        .info(new Info()
            .title("Currency Converter API")
            .description("Perform currency convertions")
            .contact(new Contact()
                .name(contactName)
                .email(contactEmail)
                .url(contactUrl))
            .version(appVersion));
  }
}

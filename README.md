# Currency Converter API

## Propósito

Disponibilizar uma API através da qual seja possível realizar conversões de moeda.

## Arquitetura

O projeto foi construído utizando arquitetura __MVC - Model View Controller__, representados, respectivamente,
pelas camadas (pacotes) ___model___, ___api___ e __service__. Existem ainda outros pacotes auxiliares
com as implementações adjacentes (como banco de dados e api externa, por exemplo) que podem ser 
substituídas sem ônus para as três camadas principais graças ao baixo acoplamento.

Um pré-requisito do projeto era ser implementado com programação reativa. Para garantir isso, foram
utilizadas as tecnologias abaixo:

- [Spring WebFlux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
para entrada e saída via HTTP.  
- Banco de dados [H2](https://www.h2database.com/) com o [Spring Data R2DBC](https://spring.io/projects/spring-data-r2dbc).

## Observabilidade

### Logs

Os logs são gerenciados pelo [SLF4J](http://www.slf4j.org/), e utiliza o [Logback](http://logback.qos.ch/) como implementação.

Localmente os logs podem ser visualizados pelo terminal, com o comando abaixo:

> docker-compose logs currencyconverter

## Integração Contínua

[![Automated Testing](https://github.com/paulosalonso/currencyconverter/actions/workflows/automated-testing.yml/badge.svg)](https://github.com/paulosalonso/currencyconverter/actions/workflows/automated-testing.yml)
[![Mutation Testing](https://github.com/paulosalonso/currencyconverter/actions/workflows/mutation-testing.yml/badge.svg)](https://github.com/paulosalonso/currencyconverter/actions/workflows/mutation-testing.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=paulosalonso_currencyconverter&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=paulosalonso_currencyconverter)

O processo de integração contínua garante a qualidade permitindo que apenas código testado seja 
introduzido na aplicação.  
É utilizado o [JaCoCo](https://www.jacoco.org/) para validação de cobertura mínima e o
[PIT Mutation](https://pitest.org/) para garantir cobertura em caso de alterações de código.
Além disso, é realizada uma análise estática com o [SonarCloud](https://sonarcloud.io/dashboard?id=paulosalonso_research).

## Deploy Contínuo

[![Heroku Homologation Environment Deploy](https://github.com/paulosalonso/currencyconverter/actions/workflows/deploy-heroku-hmg.yml/badge.svg)](https://github.com/paulosalonso/currencyconverter/actions/workflows/deploy-heroku-hmg.yml)
[![Heroku Production Environment Deploy](https://github.com/paulosalonso/currencyconverter/actions/workflows/deploy-heroku-prd.yml/badge.svg)](https://github.com/paulosalonso/currencyconverter/actions/workflows/deploy-heroku-prd.yml)

O deploy no ambiente de homologação do Heroku é realizado ao criar uma pre-release.  
O deploy no ambiente de produção do Heroku é realizado ao criar uma release.

URL de homologação: https://currencyconverter-hmg.herokuapp.com  
URL de produção: https://currencyconverter-prd.herokuapp.com

## Consumo

### User ID

Ao consumer a API é necessário enviar um ___userId___. Para obter um userId é preciso criar uma conta
(gratúita) no site https://exchangeratesapi.io.  
Após criar a conta será fornecida uma ___access_key___
que deverá ser informada nas requisições nos parâmetros identificados como ___userId___.

### Postman

Importe a coleção e os ambientes do postman presentes no projeto para consumir a API.

[Collection](https://github.com/paulosalonso/currencyconverter/blob/main/.postman/Currency%20Converter%20API.postman_collection.json)  
[Local Environment](https://github.com/paulosalonso/currencyconverter/blob/main/.postman/Currency%20Converter%20LOCAL.postman_environment.json)  
[Heroku Homologation Environment](https://github.com/paulosalonso/currencyconverter/blob/main/.postman/Currency%20Converter%20HML.postman_environment.json)  
[Heroku Production Environment](https://github.com/paulosalonso/currencyconverter/blob/main/.postman/Currency%20Converter%20PRD.postman_environment.json)

### Swagger

Também é possível consumir a API via Swagger.

[Swagger local (baseado no docker-compose)](http://localhost:8080/swagger-ui.html)  
[Swagger no Heroku - Homologação](https://currencyconverter-hmg.herokuapp.com/swagger-ui.html)  
[Swagger no Heroku - Produção](https://currencyconverter-prd.herokuapp.com/swagger-ui.html)

## Execução Local

### IDE

Para executar a aplicação na IDE basta importar o projeto e executar a classe com.github.paulosalonso.currencyconverter.CurrencyConverterApplication como uma aplicação Java.

### Maven

> mvn spring-boot:run

### java -jar
> mvn clean package \
> java -jar target/currencyconverter-<version>.jar

### Docker

Para rodar um container Docker da aplicação a partir da última versão disponível da 
[imagem no Docker Hub](https://hub.docker.com/repository/docker/paulosalonso/currencyconverter), 
execute o comando abaixo na raiz do projeto:

> docker-compose up -d

Em todos os casos a aplicação estará disponível no endereço http://localhost:8080.
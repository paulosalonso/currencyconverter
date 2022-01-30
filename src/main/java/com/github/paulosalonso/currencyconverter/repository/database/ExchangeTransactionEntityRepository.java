package com.github.paulosalonso.currencyconverter.repository.database;

import com.github.paulosalonso.currencyconverter.repository.database.entity.ExchangeTransactionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ExchangeTransactionEntityRepository
    extends ReactiveCrudRepository<ExchangeTransactionEntity, String> {

  Flux<ExchangeTransactionEntity> findAllByUserId(String userId);
}

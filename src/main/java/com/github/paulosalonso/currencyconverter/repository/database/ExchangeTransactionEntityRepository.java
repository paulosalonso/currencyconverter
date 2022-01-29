package com.github.paulosalonso.currencyconverter.repository.database;

import com.github.paulosalonso.currencyconverter.repository.database.entity.ExchangeTransactionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ExchangeTransactionEntityRepository
    extends ReactiveCrudRepository<ExchangeTransactionEntity, String> {}

package com.orthoworks.store.repository;

import com.orthoworks.store.domain.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Transaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, Long>, TransactionRepositoryInternal {
    Flux<Transaction> findAllBy(Pageable pageable);

    @Query("SELECT * FROM transaction entity WHERE entity.collection_id = :id")
    Flux<Transaction> findByCollection(Long id);

    @Query("SELECT * FROM transaction entity WHERE entity.collection_id IS NULL")
    Flux<Transaction> findAllWhereCollectionIsNull();

    @Query("SELECT * FROM transaction entity WHERE entity.id not in (select transaction_id from output)")
    Flux<Transaction> findAllWhereResultIsNull();

    @Override
    <S extends Transaction> Mono<S> save(S entity);

    @Override
    Flux<Transaction> findAll();

    @Override
    Mono<Transaction> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TransactionRepositoryInternal {
    <S extends Transaction> Mono<S> save(S entity);

    Flux<Transaction> findAllBy(Pageable pageable);

    Flux<Transaction> findAll();

    Mono<Transaction> findById(Long id);

    Flux<Transaction> findAllBy(Pageable pageable, Criteria criteria);
}

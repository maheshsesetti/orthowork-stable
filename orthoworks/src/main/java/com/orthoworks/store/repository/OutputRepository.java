package com.orthoworks.store.repository;

import com.orthoworks.store.domain.Output;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Output entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OutputRepository extends ReactiveCrudRepository<Output, Long>, OutputRepositoryInternal {
    Flux<Output> findAllBy(Pageable pageable);

    @Query("SELECT * FROM output entity WHERE entity.transaction_id = :id")
    Flux<Output> findByTransaction(Long id);

    @Query("SELECT * FROM output entity WHERE entity.transaction_id IS NULL")
    Flux<Output> findAllWhereTransactionIsNull();

    @Override
    <S extends Output> Mono<S> save(S entity);

    @Override
    Flux<Output> findAll();

    @Override
    Mono<Output> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface OutputRepositoryInternal {
    <S extends Output> Mono<S> save(S entity);

    Flux<Output> findAllBy(Pageable pageable);

    Flux<Output> findAll();

    Mono<Output> findById(Long id);

    Flux<Output> findAllBy(Pageable pageable, Criteria criteria);
}

package com.orthoworks.store.repository;

import com.orthoworks.store.domain.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Data entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DataRepository extends ReactiveCrudRepository<Data, Long>, DataRepositoryInternal {
    Flux<Data> findAllBy(Pageable pageable);

    @Query("SELECT * FROM data entity WHERE entity.transaction_id = :id")
    Flux<Data> findByTransaction(Long id);

    @Query("SELECT * FROM data entity WHERE entity.transaction_id IS NULL")
    Flux<Data> findAllWhereTransactionIsNull();

    @Override
    <S extends Data> Mono<S> save(S entity);

    @Override
    Flux<Data> findAll();

    @Override
    Mono<Data> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DataRepositoryInternal {
    <S extends Data> Mono<S> save(S entity);

    Flux<Data> findAllBy(Pageable pageable);

    Flux<Data> findAll();

    Mono<Data> findById(Long id);

    Flux<Data> findAllBy(Pageable pageable, Criteria criteria);
}

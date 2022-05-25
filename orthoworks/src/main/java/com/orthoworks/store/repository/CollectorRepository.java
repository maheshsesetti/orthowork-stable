package com.orthoworks.store.repository;

import com.orthoworks.store.domain.Collector;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Collector entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CollectorRepository extends ReactiveCrudRepository<Collector, Long>, CollectorRepositoryInternal {
    @Override
    <S extends Collector> Mono<S> save(S entity);

    @Override
    Flux<Collector> findAll();

    @Override
    Mono<Collector> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CollectorRepositoryInternal {
    <S extends Collector> Mono<S> save(S entity);

    Flux<Collector> findAllBy(Pageable pageable);

    Flux<Collector> findAll();

    Mono<Collector> findById(Long id);

    Flux<Collector> findAllBy(Pageable pageable, Criteria criteria);
}

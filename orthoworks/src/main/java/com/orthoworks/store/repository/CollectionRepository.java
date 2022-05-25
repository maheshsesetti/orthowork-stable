package com.orthoworks.store.repository;

import com.orthoworks.store.domain.Collection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Collection entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CollectionRepository extends ReactiveCrudRepository<Collection, Long>, CollectionRepositoryInternal {
    Flux<Collection> findAllBy(Pageable pageable);

    @Override
    <S extends Collection> Mono<S> save(S entity);

    @Override
    Flux<Collection> findAll();

    @Override
    Mono<Collection> findById(String id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CollectionRepositoryInternal {
    <S extends Collection> Mono<S> save(S entity);

    Flux<Collection> findAllBy(Pageable pageable);

    Flux<Collection> findAll();

    Mono<Collection> findById(String id);

    Flux<Collection> findAllBy(Pageable pageable, Criteria criteria);
}

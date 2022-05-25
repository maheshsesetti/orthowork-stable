package com.orthoworks.store.repository;

import com.orthoworks.store.domain.Feature;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Feature entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FeatureRepository extends ReactiveCrudRepository<Feature, Long>, FeatureRepositoryInternal {
    @Query("SELECT * FROM feature entity WHERE entity.collection_id = :id")
    Flux<Feature> findByCollection(Long id);

    @Query("SELECT * FROM feature entity WHERE entity.collection_id IS NULL")
    Flux<Feature> findAllWhereCollectionIsNull();

    @Override
    <S extends Feature> Mono<S> save(S entity);

    @Override
    Flux<Feature> findAll();

    @Override
    Mono<Feature> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface FeatureRepositoryInternal {
    <S extends Feature> Mono<S> save(S entity);

    Flux<Feature> findAllBy(Pageable pageable);

    Flux<Feature> findAll();

    Mono<Feature> findById(Long id);

    Flux<Feature> findAllBy(Pageable pageable, Criteria criteria);
}

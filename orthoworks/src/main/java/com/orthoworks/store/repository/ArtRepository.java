package com.orthoworks.store.repository;

import com.orthoworks.store.domain.Art;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Art entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ArtRepository extends ReactiveCrudRepository<Art, Long>, ArtRepositoryInternal {
    Flux<Art> findAllBy(Pageable pageable);

    @Override
    Mono<Art> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Art> findAllWithEagerRelationships();

    @Override
    Flux<Art> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM art entity JOIN rel_art__collection joinTable ON entity.id = joinTable.art_id WHERE joinTable.collection_id = :id"
    )
    Flux<Art> findByCollection(Long id);

    @Override
    <S extends Art> Mono<S> save(S entity);

    @Override
    Flux<Art> findAll();

    @Override
    Mono<Art> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ArtRepositoryInternal {
    <S extends Art> Mono<S> save(S entity);

    Flux<Art> findAllBy(Pageable pageable);

    Flux<Art> findAll();

    Mono<Art> findById(Long id);

    Flux<Art> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Art> findOneWithEagerRelationships(Long id);

    Flux<Art> findAllWithEagerRelationships();

    Flux<Art> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}

package com.orthoworks.store.repository;

import com.orthoworks.store.domain.Artist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Artist entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ArtistRepository extends ReactiveCrudRepository<Artist, Long>, ArtistRepositoryInternal {
    @Override
    <S extends Artist> Mono<S> save(S entity);

    @Override
    Flux<Artist> findAll();

    @Override
    Mono<Artist> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ArtistRepositoryInternal {
    <S extends Artist> Mono<S> save(S entity);

    Flux<Artist> findAllBy(Pageable pageable);

    Flux<Artist> findAll();

    Mono<Artist> findById(Long id);

    Flux<Artist> findAllBy(Pageable pageable, Criteria criteria);
}

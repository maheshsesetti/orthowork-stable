package com.orthoworks.store.repository;

import com.orthoworks.store.domain.Brand;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Brand entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BrandRepository extends ReactiveCrudRepository<Brand, Long>, BrandRepositoryInternal {
    Flux<Brand> findAllBy(Pageable pageable);

    @Override
    <S extends Brand> Mono<S> save(S entity);

    @Override
    Flux<Brand> findAll();

    @Override
    Mono<Brand> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface BrandRepositoryInternal {
    <S extends Brand> Mono<S> save(S entity);

    Flux<Brand> findAllBy(Pageable pageable);

    Flux<Brand> findAll();

    Mono<Brand> findById(Long id);

    Flux<Brand> findAllBy(Pageable pageable, Criteria criteria);
}

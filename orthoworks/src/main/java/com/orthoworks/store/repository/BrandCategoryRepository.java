package com.orthoworks.store.repository;

import com.orthoworks.store.domain.BrandCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the BrandCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BrandCategoryRepository extends ReactiveCrudRepository<BrandCategory, Long>, BrandCategoryRepositoryInternal {
    Flux<BrandCategory> findAllBy(Pageable pageable);

    @Override
    Mono<BrandCategory> findOneWithEagerRelationships(Long id);

    @Override
    Flux<BrandCategory> findAllWithEagerRelationships();

    @Override
    Flux<BrandCategory> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM brand_category entity WHERE entity.parent_id = :id")
    Flux<BrandCategory> findByParent(Long id);

    @Query("SELECT * FROM brand_category entity WHERE entity.parent_id IS NULL")
    Flux<BrandCategory> findAllWhereParentIsNull();

    @Query(
        "SELECT entity.* FROM brand_category entity JOIN rel_brand_category__brand joinTable ON entity.id = joinTable.brand_category_id WHERE joinTable.brand_id = :id"
    )
    Flux<BrandCategory> findByBrand(Long id);

    @Override
    <S extends BrandCategory> Mono<S> save(S entity);

    @Override
    Flux<BrandCategory> findAll();

    @Override
    Mono<BrandCategory> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface BrandCategoryRepositoryInternal {
    <S extends BrandCategory> Mono<S> save(S entity);

    Flux<BrandCategory> findAllBy(Pageable pageable);

    Flux<BrandCategory> findAll();

    Mono<BrandCategory> findById(Long id);

    Flux<BrandCategory> findAllBy(Pageable pageable, Criteria criteria);

    Mono<BrandCategory> findOneWithEagerRelationships(Long id);

    Flux<BrandCategory> findAllWithEagerRelationships();

    Flux<BrandCategory> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}

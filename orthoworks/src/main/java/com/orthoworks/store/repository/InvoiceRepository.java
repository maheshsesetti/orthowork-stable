package com.orthoworks.store.repository;

import com.orthoworks.store.domain.Invoice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Invoice entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InvoiceRepository extends ReactiveCrudRepository<Invoice, Long>, InvoiceRepositoryInternal {
    Flux<Invoice> findAllBy(Pageable pageable);

    @Override
    <S extends Invoice> Mono<S> save(S entity);

    @Override
    Flux<Invoice> findAll();

    @Override
    Mono<Invoice> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface InvoiceRepositoryInternal {
    <S extends Invoice> Mono<S> save(S entity);

    Flux<Invoice> findAllBy(Pageable pageable);

    Flux<Invoice> findAll();

    Mono<Invoice> findById(Long id);

    Flux<Invoice> findAllBy(Pageable pageable, Criteria criteria);
}

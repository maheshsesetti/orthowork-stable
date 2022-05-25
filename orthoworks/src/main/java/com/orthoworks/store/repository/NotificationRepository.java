package com.orthoworks.store.repository;

import com.orthoworks.store.domain.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends ReactiveCrudRepository<Notification, Long>, NotificationRepositoryInternal {
    @Override
    <S extends Notification> Mono<S> save(S entity);

    @Override
    Flux<Notification> findAll();

    @Override
    Mono<Notification> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface NotificationRepositoryInternal {
    <S extends Notification> Mono<S> save(S entity);

    Flux<Notification> findAllBy(Pageable pageable);

    Flux<Notification> findAll();

    Mono<Notification> findById(Long id);

    Flux<Notification> findAllBy(Pageable pageable, Criteria criteria);
}

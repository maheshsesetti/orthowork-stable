package com.orthoworks.store.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.orthoworks.store.domain.Collection;
import com.orthoworks.store.domain.enumeration.AuctionType;
import com.orthoworks.store.domain.enumeration.CollectionType;
import com.orthoworks.store.domain.enumeration.Currency;
import com.orthoworks.store.repository.rowmapper.CollectionRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import org.springframework.security.access.prepost.PreAuthorize;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Collection entity.
 */
@SuppressWarnings("unused")
class CollectionRepositoryInternalImpl extends SimpleR2dbcRepository<Collection, Long> implements CollectionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CollectionRowMapper collectionMapper;

    private static final Table entityTable = Table.aliased("collection", EntityManager.ENTITY_ALIAS);

    public CollectionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CollectionRowMapper collectionMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Collection.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.collectionMapper = collectionMapper;
    }

    @Override
    public Flux<Collection> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Collection> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Collection> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = CollectionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, Collection.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    @PreAuthorize("permitAll()")
    public Flux<Collection> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Collection> findById(String id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".name").is(id)).one();
    }

    private Collection process(Row row, RowMetadata metadata) {
        Collection entity = collectionMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Collection> Mono<S> save(S entity) {
        return super.save(entity);
    }
}

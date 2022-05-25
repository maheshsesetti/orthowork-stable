package com.orthoworks.store.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.orthoworks.store.domain.Transaction;
import com.orthoworks.store.domain.enumeration.TransactionStatus;
import com.orthoworks.store.repository.rowmapper.CollectionRowMapper;
import com.orthoworks.store.repository.rowmapper.TransactionRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Transaction entity.
 */
@SuppressWarnings("unused")
class TransactionRepositoryInternalImpl extends SimpleR2dbcRepository<Transaction, Long> implements TransactionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CollectionRowMapper collectionMapper;
    private final TransactionRowMapper transactionMapper;

    private static final Table entityTable = Table.aliased("transaction", EntityManager.ENTITY_ALIAS);
    private static final Table collectionTable = Table.aliased("collection", "collection");

    public TransactionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CollectionRowMapper collectionMapper,
        TransactionRowMapper transactionMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Transaction.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.collectionMapper = collectionMapper;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public Flux<Transaction> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Transaction> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Transaction> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = TransactionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CollectionSqlHelper.getColumns(collectionTable, "collection"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(collectionTable)
            .on(Column.create("collection_id", entityTable))
            .equals(Column.create("id", collectionTable));

        String select = entityManager.createSelect(selectFrom, Transaction.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Transaction> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Transaction> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    private Transaction process(Row row, RowMetadata metadata) {
        Transaction entity = transactionMapper.apply(row, "e");
        entity.setCollection(collectionMapper.apply(row, "collection"));
        return entity;
    }

    @Override
    public <S extends Transaction> Mono<S> save(S entity) {
        return super.save(entity);
    }
}

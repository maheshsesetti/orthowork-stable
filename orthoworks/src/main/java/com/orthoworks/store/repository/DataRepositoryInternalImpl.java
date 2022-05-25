package com.orthoworks.store.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.orthoworks.store.domain.Data;
import com.orthoworks.store.repository.rowmapper.DataRowMapper;
import com.orthoworks.store.repository.rowmapper.TransactionRowMapper;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Data entity.
 */
@SuppressWarnings("unused")
class DataRepositoryInternalImpl extends SimpleR2dbcRepository<Data, Long> implements DataRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TransactionRowMapper transactionMapper;
    private final DataRowMapper dataMapper;

    private static final Table entityTable = Table.aliased("data", EntityManager.ENTITY_ALIAS);
    private static final Table transactionTable = Table.aliased("transaction", "e_transaction");

    public DataRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TransactionRowMapper transactionMapper,
        DataRowMapper dataMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Data.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.transactionMapper = transactionMapper;
        this.dataMapper = dataMapper;
    }

    @Override
    public Flux<Data> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Data> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Data> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = DataSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TransactionSqlHelper.getColumns(transactionTable, "transaction"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(transactionTable)
            .on(Column.create("transaction_id", entityTable))
            .equals(Column.create("id", transactionTable));

        String select = entityManager.createSelect(selectFrom, Data.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Data> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Data> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    private Data process(Row row, RowMetadata metadata) {
        Data entity = dataMapper.apply(row, "e");
        entity.setTransaction(transactionMapper.apply(row, "transaction"));
        return entity;
    }

    @Override
    public <S extends Data> Mono<S> save(S entity) {
        return super.save(entity);
    }
}

package com.orthoworks.store.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.orthoworks.store.domain.Art;
import com.orthoworks.store.domain.Collection;
import com.orthoworks.store.domain.enumeration.AssetType;
import com.orthoworks.store.domain.enumeration.Type;
import com.orthoworks.store.repository.rowmapper.ArtRowMapper;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Art entity.
 */
@SuppressWarnings("unused")
class ArtRepositoryInternalImpl extends SimpleR2dbcRepository<Art, Long> implements ArtRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ArtRowMapper artMapper;

    private static final Table entityTable = Table.aliased("art", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable collectionLink = new EntityManager.LinkTable(
        "rel_art__collection",
        "art_id",
        "collection_id"
    );

    public ArtRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ArtRowMapper artMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Art.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.artMapper = artMapper;
    }

    @Override
    public Flux<Art> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Art> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Art> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = ArtSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, Art.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Art> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Art> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    @Override
    public Mono<Art> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Art> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Art> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Art process(Row row, RowMetadata metadata) {
        Art entity = artMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Art> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Art> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(collectionLink, entity.getId(), entity.getCollections().stream().map(Collection::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(collectionLink, entityId);
    }
}

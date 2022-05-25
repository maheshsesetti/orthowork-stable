package com.orthoworks.store.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.orthoworks.store.domain.Brand;
import com.orthoworks.store.domain.BrandCategory;
import com.orthoworks.store.repository.rowmapper.BrandCategoryRowMapper;
import com.orthoworks.store.repository.rowmapper.BrandCategoryRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
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
 * Spring Data SQL reactive custom repository implementation for the BrandCategory entity.
 */
@SuppressWarnings("unused")
class BrandCategoryRepositoryInternalImpl extends SimpleR2dbcRepository<BrandCategory, Long> implements BrandCategoryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final BrandCategoryRowMapper brandcategoryMapper;

    private static final Table entityTable = Table.aliased("brand_category", EntityManager.ENTITY_ALIAS);
    private static final Table parentTable = Table.aliased("brand_category", "parent");

    private static final EntityManager.LinkTable brandLink = new EntityManager.LinkTable(
        "rel_brand_category__brand",
        "brand_category_id",
        "brand_id"
    );

    public BrandCategoryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        BrandCategoryRowMapper brandcategoryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(BrandCategory.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.brandcategoryMapper = brandcategoryMapper;
    }

    @Override
    public Flux<BrandCategory> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<BrandCategory> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<BrandCategory> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = BrandCategorySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(BrandCategorySqlHelper.getColumns(parentTable, "parent"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(parentTable)
            .on(Column.create("parent_id", entityTable))
            .equals(Column.create("id", parentTable));

        String select = entityManager.createSelect(selectFrom, BrandCategory.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<BrandCategory> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<BrandCategory> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    @Override
    public Mono<BrandCategory> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<BrandCategory> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<BrandCategory> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private BrandCategory process(Row row, RowMetadata metadata) {
        BrandCategory entity = brandcategoryMapper.apply(row, "e");
        entity.setParent(brandcategoryMapper.apply(row, "parent"));
        return entity;
    }

    @Override
    public <S extends BrandCategory> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends BrandCategory> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager.updateLinkTable(brandLink, entity.getId(), entity.getBrands().stream().map(Brand::getId)).then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(brandLink, entityId);
    }
}

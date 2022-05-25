package com.orthoworks.store.repository.rowmapper;

import com.orthoworks.store.domain.BrandCategory;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link BrandCategory}, with proper type conversions.
 */
@Service
public class BrandCategoryRowMapper implements BiFunction<Row, String, BrandCategory> {

    private final ColumnConverter converter;

    public BrandCategoryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link BrandCategory} stored in the database.
     */
    @Override
    public BrandCategory apply(Row row, String prefix) {
        BrandCategory entity = new BrandCategory();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setSortOrder(converter.fromRow(row, prefix + "_sort_order", Integer.class));
        entity.setDateAdded(converter.fromRow(row, prefix + "_date_added", LocalDate.class));
        entity.setDateModified(converter.fromRow(row, prefix + "_date_modified", LocalDate.class));
        entity.setParentId(converter.fromRow(row, prefix + "_parent_id", Long.class));
        return entity;
    }
}

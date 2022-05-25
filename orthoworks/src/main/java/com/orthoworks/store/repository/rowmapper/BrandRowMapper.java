package com.orthoworks.store.repository.rowmapper;

import com.orthoworks.store.domain.Brand;
import com.orthoworks.store.domain.enumeration.BrandStatus;
import com.orthoworks.store.domain.enumeration.Size;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Brand}, with proper type conversions.
 */
@Service
public class BrandRowMapper implements BiFunction<Row, String, Brand> {

    private final ColumnConverter converter;

    public BrandRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Brand} stored in the database.
     */
    @Override
    public Brand apply(Row row, String prefix) {
        Brand entity = new Brand();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setKeywords(converter.fromRow(row, prefix + "_keywords", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setImageContentType(converter.fromRow(row, prefix + "_image_content_type", String.class));
        entity.setImage(converter.fromRow(row, prefix + "_image", byte[].class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Integer.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", BrandStatus.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", BigDecimal.class));
        entity.setBrandSize(converter.fromRow(row, prefix + "_brand_size", Size.class));
        entity.setDateAdded(converter.fromRow(row, prefix + "_date_added", LocalDate.class));
        entity.setDateModified(converter.fromRow(row, prefix + "_date_modified", LocalDate.class));
        return entity;
    }
}

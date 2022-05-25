package com.orthoworks.store.repository.rowmapper;

import com.orthoworks.store.domain.Art;
import com.orthoworks.store.domain.enumeration.AssetType;
import com.orthoworks.store.domain.enumeration.Type;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Art}, with proper type conversions.
 */
@Service
public class ArtRowMapper implements BiFunction<Row, String, Art> {

    private final ColumnConverter converter;

    public ArtRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Art} stored in the database.
     */
    @Override
    public Art apply(Row row, String prefix) {
        Art entity = new Art();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setHandle(converter.fromRow(row, prefix + "_handle", String.class));
        entity.setAssetType(converter.fromRow(row, prefix + "_asset_type", AssetType.class));
        entity.setType(converter.fromRow(row, prefix + "_type", Type.class));
        return entity;
    }
}

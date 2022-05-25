package com.orthoworks.store.repository.rowmapper;

import com.orthoworks.store.domain.Collection;
import com.orthoworks.store.domain.enumeration.AuctionType;
import com.orthoworks.store.domain.enumeration.CollectionType;
import com.orthoworks.store.domain.enumeration.Currency;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Collection}, with proper type conversions.
 */
@Service
public class CollectionRowMapper implements BiFunction<Row, String, Collection> {

    private final ColumnConverter converter;

    public CollectionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Collection} stored in the database.
     */
    @Override
    public Collection apply(Row row, String prefix) {
        Collection entity = new Collection();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setCount(converter.fromRow(row, prefix + "_count", Integer.class));
        entity.setCollectionType(converter.fromRow(row, prefix + "_collection_type", CollectionType.class));
        entity.setAuctionType(converter.fromRow(row, prefix + "_auction_type", AuctionType.class));
        entity.setMinRange(converter.fromRow(row, prefix + "_min_range", Float.class));
        entity.setMaxRange(converter.fromRow(row, prefix + "_max_range", Float.class));
        entity.setCurrency(converter.fromRow(row, prefix + "_currency", Currency.class));
        entity.setOwner(converter.fromRow(row, prefix + "_owner", String.class));
        return entity;
    }
}

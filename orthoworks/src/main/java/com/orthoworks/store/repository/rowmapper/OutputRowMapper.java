package com.orthoworks.store.repository.rowmapper;

import com.orthoworks.store.domain.Output;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Output}, with proper type conversions.
 */
@Service
public class OutputRowMapper implements BiFunction<Row, String, Output> {

    private final ColumnConverter converter;

    public OutputRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Output} stored in the database.
     */
    @Override
    public Output apply(Row row, String prefix) {
        Output entity = new Output();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", Instant.class));
        entity.setResult(converter.fromRow(row, prefix + "_result", String.class));
        entity.setTransactionId(converter.fromRow(row, prefix + "_transaction_id", Long.class));
        return entity;
    }
}

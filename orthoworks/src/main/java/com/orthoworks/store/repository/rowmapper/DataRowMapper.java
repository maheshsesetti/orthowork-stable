package com.orthoworks.store.repository.rowmapper;

import com.orthoworks.store.domain.Data;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Data}, with proper type conversions.
 */
@Service
public class DataRowMapper implements BiFunction<Row, String, Data> {

    private final ColumnConverter converter;

    public DataRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Data} stored in the database.
     */
    @Override
    public Data apply(Row row, String prefix) {
        Data entity = new Data();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setFileContentType(converter.fromRow(row, prefix + "_file_content_type", String.class));
        entity.setFile(converter.fromRow(row, prefix + "_file", byte[].class));
        entity.setTransactionId(converter.fromRow(row, prefix + "_transaction_id", Long.class));
        return entity;
    }
}

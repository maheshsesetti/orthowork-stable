package com.orthoworks.store.repository.rowmapper;

import com.orthoworks.store.domain.Transaction;
import com.orthoworks.store.domain.enumeration.TransactionStatus;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Transaction}, with proper type conversions.
 */
@Service
public class TransactionRowMapper implements BiFunction<Row, String, Transaction> {

    private final ColumnConverter converter;

    public TransactionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Transaction} stored in the database.
     */
    @Override
    public Transaction apply(Row row, String prefix) {
        Transaction entity = new Transaction();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", TransactionStatus.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", Instant.class));
        entity.setCollectionId(converter.fromRow(row, prefix + "_collection_id", Long.class));
        return entity;
    }
}

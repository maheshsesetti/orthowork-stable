package com.orthoworks.store.repository.rowmapper;

import com.orthoworks.store.domain.Notification;
import com.orthoworks.store.domain.enumeration.NotificationType;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Notification}, with proper type conversions.
 */
@Service
public class NotificationRowMapper implements BiFunction<Row, String, Notification> {

    private final ColumnConverter converter;

    public NotificationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Notification} stored in the database.
     */
    @Override
    public Notification apply(Row row, String prefix) {
        Notification entity = new Notification();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", Instant.class));
        entity.setDetails(converter.fromRow(row, prefix + "_details", String.class));
        entity.setSentDate(converter.fromRow(row, prefix + "_sent_date", Instant.class));
        entity.setFormat(converter.fromRow(row, prefix + "_format", NotificationType.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setBrandId(converter.fromRow(row, prefix + "_brand_id", Long.class));
        return entity;
    }
}

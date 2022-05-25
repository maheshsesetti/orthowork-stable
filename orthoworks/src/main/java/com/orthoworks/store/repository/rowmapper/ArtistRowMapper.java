package com.orthoworks.store.repository.rowmapper;

import com.orthoworks.store.domain.Artist;
import com.orthoworks.store.domain.enumeration.Gender;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Artist}, with proper type conversions.
 */
@Service
public class ArtistRowMapper implements BiFunction<Row, String, Artist> {

    private final ColumnConverter converter;

    public ArtistRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Artist} stored in the database.
     */
    @Override
    public Artist apply(Row row, String prefix) {
        Artist entity = new Artist();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setGender(converter.fromRow(row, prefix + "_gender", Gender.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        entity.setAddressLine1(converter.fromRow(row, prefix + "_address_line_1", String.class));
        entity.setAddressLine2(converter.fromRow(row, prefix + "_address_line_2", String.class));
        entity.setCity(converter.fromRow(row, prefix + "_city", String.class));
        entity.setCountry(converter.fromRow(row, prefix + "_country", String.class));
        return entity;
    }
}

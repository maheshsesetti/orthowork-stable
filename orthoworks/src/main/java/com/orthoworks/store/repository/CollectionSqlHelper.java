package com.orthoworks.store.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CollectionSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("title", table, columnPrefix + "_title"));
        columns.add(Column.aliased("count", table, columnPrefix + "_count"));
        columns.add(Column.aliased("collection_type", table, columnPrefix + "_collection_type"));
        columns.add(Column.aliased("auction_type", table, columnPrefix + "_auction_type"));
        columns.add(Column.aliased("min_range", table, columnPrefix + "_min_range"));
        columns.add(Column.aliased("max_range", table, columnPrefix + "_max_range"));
        columns.add(Column.aliased("currency", table, columnPrefix + "_currency"));
        columns.add(Column.aliased("owner", table, columnPrefix + "_owner"));

        return columns;
    }
}

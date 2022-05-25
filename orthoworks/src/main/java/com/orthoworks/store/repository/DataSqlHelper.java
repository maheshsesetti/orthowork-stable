package com.orthoworks.store.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class DataSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("file", table, columnPrefix + "_file"));
        columns.add(Column.aliased("file_content_type", table, columnPrefix + "_file_content_type"));

        columns.add(Column.aliased("transaction_id", table, columnPrefix + "_transaction_id"));
        return columns;
    }
}

package com.orthoworks.store.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class BrandCategorySqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("sort_order", table, columnPrefix + "_sort_order"));
        columns.add(Column.aliased("date_added", table, columnPrefix + "_date_added"));
        columns.add(Column.aliased("date_modified", table, columnPrefix + "_date_modified"));

        columns.add(Column.aliased("parent_id", table, columnPrefix + "_parent_id"));
        return columns;
    }
}

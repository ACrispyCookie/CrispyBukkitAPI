package dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.structure;

import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.queries.builders.table.QueryBuilderCreateTable;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.structure.column.SqlType;

public class SqlColumn {

    private final String name;
    private final SqlType type;
    private final Integer size;

    private final String defaultValue;
    private final String constraint;
    private final QueryBuilderCreateTable.ColumnData[] columnData;

    public SqlColumn(String name, SqlType type, Integer size, String defaultValue, String constraint, QueryBuilderCreateTable.ColumnData[] columnData) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.defaultValue = defaultValue;
        this.constraint = constraint;
        this.columnData = columnData;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder(name).append(" ");

        builder.append(type.h2()).append(" ");

        if (size != null)
            builder.append("(").append(size).append(")").append(" ");

        for (QueryBuilderCreateTable.ColumnData columnDatum : columnData) {
            if (columnDatum == null) continue;
            builder.append(columnDatum.getName()).append(" ");
        }


        if (defaultValue != null)
            builder.append("DEFAULT ").append(defaultValue).append(" ");

        if (constraint != null)
            builder.append(constraint);

        return builder.toString();
    }

}

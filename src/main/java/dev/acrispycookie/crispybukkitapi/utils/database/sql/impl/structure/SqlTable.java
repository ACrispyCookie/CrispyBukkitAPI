package dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.structure;

import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.structure.AbstractSqlDatabase;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.structure.AbstractSqlTable;

public class SqlTable implements AbstractSqlTable {

    private final AbstractSqlDatabase database;
    private final String tableName;

    public SqlTable(AbstractSqlDatabase database, String tableName) {
        this.database = database;
        this.tableName = tableName;
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public AbstractSqlDatabase getDatabase() {
        return database;
    }

}

package dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.structure;

import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.queries.builders.data.*;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.queries.builders.table.QueryBuilderAlterTable;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.queries.builders.table.QueryBuilderCreateTable;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.queries.builders.table.QueryBuilderDropTable;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.queries.builders.table.QueryBuilderTableExists;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.structure.AbstractSqlDatabase;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.structure.AbstractSqlTable;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.connector.SqlConnector;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.queries.builders.data.*;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.queries.builders.table.QueryAlterTable;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.queries.builders.table.QueryCreateTable;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.queries.builders.table.QueryDropTable;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.queries.builders.table.QueryTableExists;

import java.util.HashMap;
import java.util.Map;

public class SqlDatabase implements AbstractSqlDatabase {

    private final SqlConnector connector;
    private final String databaseName;

    private final Map<String, AbstractSqlTable> tables = new HashMap<>();

    public SqlDatabase(SqlConnector connector, String databaseName) {
        this.connector = connector;
        this.databaseName = databaseName;
    }


    public void addTable(AbstractSqlTable table) {
        tables.put(table.getName(), table);
    }

    public void removeTable(AbstractSqlTable table) {
        removeTable(table.getName());
    }

    public void removeTable(String tableName) {
        tables.remove(tableName);
    }

    public SqlConnector getConnector() {
        return connector;
    }

    @Override
    public String getName() {
        return databaseName;
    }

    @Override
    public QueryBuilderTableExists tableExists(String tableName) {
        return new QueryTableExists(this, tableName);
    }

    @Override
    public QueryBuilderAlterTable alterTable(String tableName) {
        return new QueryAlterTable(this, tableName);
    }

    @Override
    public QueryBuilderCreateTable createTable(String tableName) {
        return new QueryCreateTable(this, tableName);
    }

    @Override
    public QueryBuilderDropTable dropTable(String tableName) {
        return new QueryDropTable(this, tableName);
    }

    @Override
    public AbstractSqlTable useTable(String tableName) {
        return tables.get(tableName);
    }

    @Override
    public QueryBuilderSelect select() {
        return new QuerySelect(connector, databaseName);
    }

    @Override
    public QueryBuilderInsert insert() {
        return new QueryInsert(connector, databaseName);
    }

    @Override
    public QueryBuilderDelete delete() {
        return new QueryDelete(connector, databaseName);
    }

    @Override
    public QueryBuilderUpdate update() {
        return new QueryUpdate(connector, databaseName);
    }

    @Override
    public QueryBuilderReplace replace() {
        return new QueryReplace(connector, databaseName);
    }

}

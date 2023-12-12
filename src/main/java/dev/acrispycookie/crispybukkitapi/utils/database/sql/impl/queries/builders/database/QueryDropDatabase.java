package dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.queries.builders.database;

import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.queries.builders.database.QueryBuilderDropDatabase;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.queries.effective.SqlQuery;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.connector.SqlConnector;

import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

public class QueryDropDatabase extends SqlQuery implements QueryBuilderDropDatabase {

    private boolean ifExists = false;

    public QueryDropDatabase(SqlConnector connector, String databaseName) {
        super(connector, databaseName);
    }

    @Override
    public QueryBuilderDropDatabase ifExists(boolean add) {
        ifExists = add;
        return this;
    }

    @Override
    public QueryBuilderDropDatabase build() {
        StringBuilder builder = new StringBuilder("DROP DATABASE ");
        if (ifExists)
            builder.append("IF EXISTS ");
        builder.append(databaseName).append(";");
        setSql(builder.toString());
        return this;
    }

    @Override
    public PreparedStatement asPrepared() {
        return getConnector().asPrepared(getSql());
    }

    @Override
    public QueryBuilderDropDatabase exec() {
        getConnector().execute(getSql());
        return this;
    }

    @Override
    public QueryBuilderDropDatabase patternClone() {
        QueryDropDatabase copy = new QueryDropDatabase(connector, databaseName);
        copy.ifExists = ifExists;
        return copy;
    }

    @Override
    public CompletableFuture<QueryBuilderDropDatabase> execAsync() {
        return CompletableFuture.supplyAsync(this::exec);
    }

    @Override
    public String getSql() {
        return super.sql;
    }

}

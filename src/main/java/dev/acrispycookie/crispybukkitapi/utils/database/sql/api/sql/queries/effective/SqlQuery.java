package dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.queries.effective;

import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.connector.SqlConnector;

/**
 * represents the form of the query ready to run
 */
public abstract class SqlQuery {

    /**
     * main Holder, provider of the future connector.
     */
    protected SqlConnector connector;
    /**
     * Name of the present of future database
     */
    protected String databaseName;
    /**
     * The actual query
     */
    protected String sql;

    public SqlQuery() {
    }

    public SqlQuery(SqlConnector connector, String databaseName) {
        this.connector = connector;
        this.databaseName = databaseName;
    }

    protected SqlConnector getConnector() {
        return connector;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}

package dev.acrispycookie.crispybukkitapi.database;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.DatabaseLoadData;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.DatabaseSchema;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.queries.builders.database.QueryBuilderCreateDatabase;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.queries.builders.table.QueryBuilderCreateTable;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.structure.AbstractSqlDatabase;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.structure.AbstractSqlTable;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.connector.SqlConnector;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.connector.hikari.SqlConnectorImpl;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.queries.builders.database.QueryCreateDatabase;

import java.sql.SQLException;
import java.util.ArrayList;

public class RemoteDatabase extends AbstractDatabase {
    private final ArrayList<AbstractSqlTable> tables = new ArrayList<>();
    private AbstractSqlDatabase database;
    private SqlConnector connector;

    public RemoteDatabase(CrispyBukkitAPI api, DatabaseSchema schema) {
        super(api, schema);
    }

    @Override
    public void load(DatabaseLoadData data) throws DatabaseException {
        if(data.getType() != DatabaseLoadData.DatabaseType.REMOTE)
            throw new DatabaseException("Wrong load data type given!");
        try {
            connector = new SqlConnectorImpl(api, 1, 10, "pool");
            connector.connect(data.getCredentials());
            QueryBuilderCreateDatabase createDatabase = new QueryCreateDatabase(connector, data.getCredentials().database()).ifNotExists(true).build();
            database = createDatabase.execReturn();
            for (QueryBuilderCreateTable q : schema.getTables()) {
                tables.add(q.execReturn());
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void close() {
        connector.close();
    }

    public AbstractSqlDatabase get() {
        return database;
    }
}

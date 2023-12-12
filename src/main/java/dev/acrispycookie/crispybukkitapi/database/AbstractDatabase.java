package dev.acrispycookie.crispybukkitapi.database;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.DatabaseLoadData;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.DatabaseSchema;

public abstract class AbstractDatabase implements Database {

    protected final CrispyBukkitAPI api;
    protected final DatabaseSchema schema;
    public abstract void load(DatabaseLoadData data) throws DatabaseException;
    public abstract void close();

    public AbstractDatabase(CrispyBukkitAPI api, DatabaseSchema schema) {
        this.api = api;
        this.schema = schema;
    }

    @Override
    public void reload(DatabaseLoadData data) throws DatabaseException {
        close();
        load(data);
    }

    public DatabaseSchema getSchema() {
        return schema;
    }
}

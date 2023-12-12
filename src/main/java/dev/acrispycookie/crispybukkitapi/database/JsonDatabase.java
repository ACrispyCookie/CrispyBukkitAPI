package dev.acrispycookie.crispybukkitapi.database;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.DatabaseLoadData;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.DatabaseSchema;

public class JsonDatabase extends AbstractDatabase {

    public JsonDatabase(CrispyBukkitAPI api, DatabaseSchema schema) {
        super(api, schema);
    }

    @Override
    public void load(DatabaseLoadData data) throws DatabaseException {

    }

    @Override
    public void close() {

    }

}

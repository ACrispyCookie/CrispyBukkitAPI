package dev.acrispycookie.crispybukkitapi.managers.database;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;

public class SqlDatabase extends Database {

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final String tablePrefix;

    public SqlDatabase(CrispyBukkitAPI api, String host, String database, String username, String password, String tablePrefix) {
        super(api);
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.tablePrefix = tablePrefix;
    }

    @Override
    public void load() {

    }

    public void close() {

    }
}

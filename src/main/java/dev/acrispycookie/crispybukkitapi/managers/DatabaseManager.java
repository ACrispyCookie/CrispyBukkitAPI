package dev.acrispycookie.crispybukkitapi.managers;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.managers.database.Database;
import dev.acrispycookie.crispybukkitapi.managers.database.JsonDatabase;
import dev.acrispycookie.crispybukkitapi.managers.database.SqlDatabase;

public class DatabaseManager extends BaseManager {

    private DatabaseMode mode;
    private Database db;
    private boolean enabled = true;

    public DatabaseManager(CrispyBukkitAPI api) {
        super(api);
        this.mode = null;
    }

    public void load() {
        if(!api.getManager(ConfigManager.class).hasDefault())
            return;

        this.mode = DatabaseMode.valueOf(getOptionValue(DatabaseOption.TYPE).toUpperCase());
        switch (mode) {
            case SQL:
                db = new SqlDatabase(
                        api,
                        getOptionValue(DatabaseOption.HOST),
                        getOptionValue(DatabaseOption.DATABASE),
                        getOptionValue(DatabaseOption.USERNAME),
                        getOptionValue(DatabaseOption.PASSWORD),
                        getOptionValue(DatabaseOption.TABLE_PREFIX)
                );
                break;
            case FLATFILE:
                db = new JsonDatabase(api, getOptionValue(DatabaseOption.DATA_FOLDER));
                break;
            default:
                db = null;
        }
    }

    @Override
    public boolean reload() {
        if (mode == DatabaseMode.FLATFILE)
            ((JsonDatabase) db).reload();
        else {
            ((SqlDatabase) db).close();
            db = new SqlDatabase(
                    api,
                    getOptionValue(DatabaseOption.HOST),
                    getOptionValue(DatabaseOption.DATABASE),
                    getOptionValue(DatabaseOption.USERNAME),
                    getOptionValue(DatabaseOption.PASSWORD),
                    getOptionValue(DatabaseOption.TABLE_PREFIX)
            );
        }

        return false;
    }

    private String getOptionValue(DatabaseOption option) {
        return api.getManager(ConfigManager.class).getFromType(
                api.getManager(ConfigManager.class).getDefault(),
                "database." + option.getPath(), ConfigManager.DataType.STRING,
                String.class
        );
    }

    public enum DatabaseMode {
        FLATFILE,
        SQL,
        MONGODB

    }

    private enum DatabaseOption {
        TYPE("type"),
        HOST("sql.host"),
        DATABASE("sql.database"),
        USERNAME("sql.username"),
        PASSWORD("sql.password"),
        TABLE_PREFIX("sql.table-prefix"),
        DATA_FOLDER("flat.data-folder");

        private final String path;

        DatabaseOption(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }
}

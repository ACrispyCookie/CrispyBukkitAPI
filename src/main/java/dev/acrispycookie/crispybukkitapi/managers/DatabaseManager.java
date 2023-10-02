package dev.acrispycookie.crispybukkitapi.managers;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.managers.database.Database;
import dev.acrispycookie.crispybukkitapi.managers.database.JsonDatabase;
import dev.acrispycookie.crispybukkitapi.managers.database.SqlDatabase;

public class DatabaseManager extends BaseManager {

    private DatabaseMode mode;
    private Database db;
    private Database.DatabaseSchema schema = null;

    public DatabaseManager(CrispyBukkitAPI api) {
        super(api);
        this.mode = null;
    }

    public void load() throws ManagerLoadException {
        if(!api.getManager(ConfigManager.class).hasDefault())
            return;
        if(schema == null)
            throw new ManagerLoadException("Database schema was not specified");

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
    public void reload() throws ManagerReloadException {
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
    }

    public void setSchema(Database.DatabaseSchema schema) {
        this.schema = schema;
    }

    public DatabaseMode getMode() {
        return mode;
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

package dev.acrispycookie.crispybukkitapi.managers;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.database.AbstractDatabase;
import dev.acrispycookie.crispybukkitapi.database.Database;
import dev.acrispycookie.crispybukkitapi.database.JsonDatabase;
import dev.acrispycookie.crispybukkitapi.database.RemoteDatabase;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.DatabaseLoadData;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.DatabaseSchema;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.StorageCredentials;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.structure.AbstractSqlDatabase;

public class DataManager extends BaseManager {

    private DatabaseMode mode;
    private AbstractDatabase db;
    private DatabaseSchema schema;

    public DataManager(CrispyBukkitAPI api) {
        super(api);
        this.mode = null;
        this.schema = null;
    }

    public void load() throws ManagerLoadException {
        if(!api.getManager(ConfigManager.class).hasDefault() || !api.getManager(ConfigManager.class).hasPath(
                api.getManager(ConfigManager.class).getDefault(), "database"))
            return;
        if(schema == null)
            throw new ManagerLoadException("Schema was not set!");

        this.mode = DatabaseMode.valueOf(getOptionValue(DatabaseOption.TYPE).toUpperCase());
        switch (mode) {
            case SQL:
                if (schema.getType() != DatabaseSchema.SchemaType.TABLE)
                    throw new ManagerLoadException("Wrong type database schema given!");
                if (schema.getTables().isEmpty())
                    throw new ManagerLoadException("Database schema was not specified");
                db = new RemoteDatabase(api, schema);
                break;
            case FLATFILE:
                db = new JsonDatabase(api, schema);
                break;
            default:
                db = null;
        }
    }

    @Override
    public void reload() throws ManagerReloadException {
        try {
            db.reload(getLoadData());
        } catch (Database.DatabaseException e) {
            throw new ManagerReloadException(e, true, true);
        }
    }

    public DatabaseMode getMode() {
        return mode;
    }

    public AbstractSqlDatabase getDatabase() {
        return ((RemoteDatabase) db).get();
    }

    public void setSchema(DatabaseSchema schema) {
        this.schema = schema;
    }

    private DatabaseLoadData getLoadData() {
        DatabaseLoadData data;
        switch (mode) {
            case FLATFILE:
                data = new DatabaseLoadData(getOptionValue(DatabaseOption.DATA_FOLDER));
                break;
            case SQL:
                data = new DatabaseLoadData(new StorageCredentials(true, getOptionValue(DatabaseOption.HOST),
                        getOptionValue(DatabaseOption.DATABASE),
                        getOptionValue(DatabaseOption.USERNAME),
                        getOptionValue(DatabaseOption.PASSWORD),
                        getOptionValue(DatabaseOption.TABLE_PREFIX), -1, -1, -1, -1, -1));
                break;
            default:
                data = null;
        }
        return data;
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

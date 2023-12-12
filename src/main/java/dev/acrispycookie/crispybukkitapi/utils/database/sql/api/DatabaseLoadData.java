package dev.acrispycookie.crispybukkitapi.utils.database.sql.api;

public class DatabaseLoadData {

    private final StorageCredentials credentials;
    private final String file;
    private final DatabaseType type;

    public DatabaseLoadData(String file) {
        this.file = file;
        this.credentials = null;
        this.type = DatabaseType.LOCAL;
    }

    public DatabaseLoadData(StorageCredentials credentials) {
        this.credentials = credentials;
        this.file = null;
        this.type = DatabaseType.REMOTE;
    }

    public DatabaseType getType() {
        return type;
    }

    public StorageCredentials getCredentials() {
        return credentials;
    }

    public String getFile() {
        return file;
    }

    public enum DatabaseType {
        REMOTE,
        LOCAL
    }
}

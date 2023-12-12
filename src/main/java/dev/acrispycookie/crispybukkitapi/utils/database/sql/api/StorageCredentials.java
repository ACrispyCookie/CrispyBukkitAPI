package dev.acrispycookie.crispybukkitapi.utils.database.sql.api;

public class StorageCredentials {

    private final boolean enabled;
    private final String address;
    private final String database;
    private final String username;
    private final String password;
    private final String tablePrefix;
    private final int maxPoolSize;
    private final int minIdleConnections;
    private final int maxLifetime;
    private final int keepAliveTime;
    private final int connectionTimeout;

    public StorageCredentials(boolean enabled, String address, String database, String username, String password, String tablePrefix, int maxPoolSize, int minIdleConnections, int maxLifetime, int keepAliveTime, int connectionTimeout) {
        this.enabled = enabled;
        this.address = address;
        this.database = database;
        this.username = username;
        this.password = password;
        this.tablePrefix = tablePrefix;
        this.maxPoolSize = maxPoolSize;
        this.minIdleConnections = minIdleConnections;
        this.maxLifetime = maxLifetime;
        this.keepAliveTime = keepAliveTime;
        this.connectionTimeout = connectionTimeout;
    }

    public boolean enabled() {
        return enabled;
    }

    public String address() {
        return address;
    }

    public String database() {
        return database;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String tablePrefix() { return tablePrefix; }

    public int maxPoolSize() {
        return maxPoolSize;
    }

    public int minIdleConnections() {
        return minIdleConnections;
    }

    public int maxLifetime() {
        return maxLifetime;
    }

    public int keepAliveTime() {
        return keepAliveTime;
    }

    public int connectionTimeout() {
        return connectionTimeout;
    }
}

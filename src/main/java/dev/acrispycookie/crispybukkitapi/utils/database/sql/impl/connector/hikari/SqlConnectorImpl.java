package dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.connector.hikari;

import com.zaxxer.hikari.HikariConfig;
import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.connector.hikari.property.HikariProperty;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.connector.hikari.property.PropertiesProvider;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.connector.hikari.property.PropertyPair;

import java.sql.SQLException;

public class SqlConnectorImpl extends HikariConnector {

    public SqlConnectorImpl(CrispyBukkitAPI api, int minPoolSize, int maxPoolSize, String poolName) {
        super(api, minPoolSize, maxPoolSize, poolName, "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
    }

    @Override
    protected HikariConfig getConfig(int minPoolSize, int maxPoolSize, String poolName) {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minPoolSize);
        config.setPoolName(poolName);
        return config;
    }

    @Override
    protected PropertiesProvider getProperties() {
        PropertiesProvider holder = new PropertiesProvider();
        holder.withProperties(
                new PropertyPair(HikariProperty.cachePrepStmts, "true"),
                new PropertyPair(HikariProperty.alwaysSendSetIsolation, "false"),
                new PropertyPair(HikariProperty.cacheServerConfiguration, "true"),
                new PropertyPair(HikariProperty.elideSetAutoCommits, "true"),
                new PropertyPair(HikariProperty.maintainTimeStats, "false"),
                new PropertyPair(HikariProperty.useLocalSessionState, "true"),
                new PropertyPair(HikariProperty.useServerPrepStmts, "true"),
                new PropertyPair(HikariProperty.prepStmtCacheSize, "500"),
                new PropertyPair(HikariProperty.rewriteBatchedStatements, "true"),
                new PropertyPair(HikariProperty.prepStmtCacheSqlLimit, "2048"),
                new PropertyPair(HikariProperty.cacheCallableStmts, "true"),
                new PropertyPair(HikariProperty.cacheResultSetMetadata, "true"),
                new PropertyPair(HikariProperty.characterEncoding, "utf8"),
                new PropertyPair(HikariProperty.useUnicode, "true"),
                new PropertyPair(HikariProperty.zeroDateTimeBehavior, "CONVERT_TO_NULL")
        );
        return holder;
    }
    @Override
    public void close() {
        source.close();
    }

    @Override
    public void connect(String host) throws SQLException {
        super.connect("jdbc://" + host);
    }

    @Override
    public void connect(String host, String username) throws SQLException {
        super.connect("jdbc://" + host, username);
    }

    @Override
    public void connect(String host, String username, String password) throws SQLException {
        super.connect("jdbc://" + host, username, password);
    }

}

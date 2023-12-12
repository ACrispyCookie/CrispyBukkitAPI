package dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.connector.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.connector.SqlConnector;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.connector.hikari.property.PropertiesProvider;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class HikariConnector extends SqlConnector {

    protected final HikariConfig config;
    protected HikariDataSource source;

    public HikariConnector(CrispyBukkitAPI api, int minPoolSize, int maxPoolSize, String poolName, String driver) {
        super(api);
        PropertiesProvider provider = getProperties();
        if (provider == null) {
            provider = new PropertiesProvider();
        }

        config = getConfig(minPoolSize, maxPoolSize, poolName);
        config.setDriverClassName(driver);
        config.setDataSourceProperties(provider.getBuild());
    }

    public abstract void close();

    @Override
    public void connect(String host) throws SQLException {
        config.setJdbcUrl(host);
        source = new HikariDataSource(config);
        establish();
    }

    @Override
    public void connect(String host, String username) throws SQLException {
        config.setJdbcUrl(host);
        config.setUsername(username);
        source = new HikariDataSource(config);
        establish();
    }

    @Override
    public void connect(String host, String username, String password) throws SQLException {
        config.setJdbcUrl(host);
        config.setUsername(username);
        config.setPassword(password);
        source = new HikariDataSource(config);
        establish();
    }

    private void establish() throws SQLException {
        Connection connection = source.getConnection();
    }

    @Override
    protected Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    protected abstract HikariConfig getConfig(int minPoolSize, int maxPoolSize, String poolName);

    protected abstract PropertiesProvider getProperties();

}

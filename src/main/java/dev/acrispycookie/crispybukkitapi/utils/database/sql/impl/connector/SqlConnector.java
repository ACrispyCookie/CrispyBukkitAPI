package dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.connector;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.StorageCredentials;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.structure.data.QueryData;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.structure.data.QueryDataImpl;

import java.sql.*;

public abstract class SqlConnector {

    private final CrispyBukkitAPI api;

    public SqlConnector(CrispyBukkitAPI api) {
        this.api = api;
    }

    public abstract void connect(String host) throws SQLException;

    public abstract void connect(String host, String username) throws SQLException;

    public abstract void connect(String address, String username, String password) throws SQLException;

    public abstract void close();

    public void connect(StorageCredentials credentials) throws SQLException {
        connect(credentials.address(), credentials.username(), credentials.password());
    }

    public void execute(String query) {
        try {
            Statement statement = getConnection().createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public PreparedStatement asPrepared(String query) {
        try {
            return getConnection().prepareStatement(query);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public QueryData executeReturn(String query) {
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            return new QueryDataImpl(statement.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public DatabaseMetaData getMetaData() {
        try {
            return getConnection().getMetaData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCatalog() throws SQLException {
        return getConnection().getCatalog();
    }

    protected abstract Connection getConnection() throws SQLException;

}

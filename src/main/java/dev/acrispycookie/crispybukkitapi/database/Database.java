package dev.acrispycookie.crispybukkitapi.database;

import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.DatabaseLoadData;

public interface Database {
    void load(DatabaseLoadData data) throws AbstractDatabase.DatabaseException;
    void reload(DatabaseLoadData data) throws AbstractDatabase.DatabaseException;
    void close();

    class DatabaseException extends Exception {
        public DatabaseException(String s) {
            super(s);
        }
        public DatabaseException(Exception e) {
            super(e);
        }
    }
}

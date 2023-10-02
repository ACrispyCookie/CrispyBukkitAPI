package dev.acrispycookie.crispybukkitapi.managers.database;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.managers.DatabaseManager;
import org.bukkit.Bukkit;

import java.util.function.Consumer;

public abstract class Database {

    private final CrispyBukkitAPI api;
    public abstract void load();

    public Database(CrispyBukkitAPI api) {
        this.api = api;
    }

    public <T> T get(String path, Class<T> type) {
        if(api.getManager(DatabaseManager.class).getMode() != DatabaseManager.DatabaseMode.FLATFILE)
            throw new DatabaseException("Function call for wrong mode was used");

        return null;
    }

    public void getAsync(String statement, Consumer<String> callback, Object... objects) {
        Bukkit.getScheduler().runTaskAsynchronously(api.getPlugin(), () -> callback.accept(get(statement, objects)));
    }

    private String get(String statement, Object... objects) {

        return null;
    }

    public static class DatabaseSchema {
    }

    public static class DatabaseException extends RuntimeException {
        public DatabaseException(String s) {
            super(s);
        }
    }

}

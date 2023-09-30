package dev.acrispycookie.crispybukkitapi.managers.database;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class Database {

    private final CrispyBukkitAPI api;
    public abstract void load();

    public Database(CrispyBukkitAPI api) {
        this.api = api;
    }

    public String get(String path) {
        return null;
    }

    public void getAsync(String path, Consumer<String> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(api.getPlugin(), () -> callback.accept(get("")));
    }

    private void translatePathToTable(String path) {

    }

    public class DatabaseSchema {
    }

}

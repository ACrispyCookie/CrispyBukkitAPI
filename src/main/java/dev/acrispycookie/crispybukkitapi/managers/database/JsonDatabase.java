package dev.acrispycookie.crispybukkitapi.managers.database;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;

public class JsonDatabase extends Database {

    private final String directory;

    public JsonDatabase(CrispyBukkitAPI api, String directory) {
        super(api);
        this.directory = directory;
    }

    @Override
    public void load() {

    }

    public void reload() {

    }
}

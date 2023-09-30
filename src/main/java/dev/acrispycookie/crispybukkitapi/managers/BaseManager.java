package dev.acrispycookie.crispybukkitapi.managers;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;

public abstract class BaseManager {

    protected final CrispyBukkitAPI api;

    public BaseManager(CrispyBukkitAPI api) {
        this.api = api;
    }

    public abstract void load();

    public abstract boolean reload();
}

package dev.acrispycookie.crispybukkitapi.features;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import org.bukkit.event.Listener;

public abstract class FeatureListener<T extends Feature> implements Listener {

    private final CrispyBukkitAPI api;
    protected final T feature;

    public FeatureListener(T feature, CrispyBukkitAPI api) {
        this.api = api;
        this.feature = feature;
    }
}

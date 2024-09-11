package dev.acrispycookie.crispybukkitapi.features;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import org.bukkit.event.Listener;

public abstract class CrispyFeatureListener<T extends CrispyFeature<?, ?, ?, ?>> implements Listener {

    protected final CrispyBukkitAPI api;
    protected final T feature;

    public CrispyFeatureListener(T feature, CrispyBukkitAPI api) {
        this.api = api;
        this.feature = feature;
    }
}

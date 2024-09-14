package dev.acrispycookie.crispypluginapi.features;

import dev.acrispycookie.crispypluginapi.CrispyPluginAPI;

public abstract class FeatureListener<T extends CrispyFeature<?, ?, ?, ?>>  {

    private final CrispyPluginAPI api;
    protected final T feature;
    protected abstract void register();
    protected abstract void unregister();

    public FeatureListener(T feature, CrispyPluginAPI api) {
        this.api = api;
        this.feature = feature;
    }

    public CrispyPluginAPI getApi() {
        return api;
    }
}

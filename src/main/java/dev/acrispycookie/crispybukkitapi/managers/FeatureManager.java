package dev.acrispycookie.crispybukkitapi.managers;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.features.CrispyFeature;
import dev.acrispycookie.crispycommons.utility.logging.CrispyLogger;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class FeatureManager extends BaseManager {

    private final Map<String, CrispyFeature<?, ?, ?, ?>> features;

    public FeatureManager(CrispyBukkitAPI api) {
        super(api);
        this.features = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public void registerFeature(Class<? extends CrispyFeature<?, ?, ?, ?>> fClass) {
        try {
            CrispyFeature<?, ?, ?, ?> feature = (CrispyFeature<?, ?, ?, ?>) fClass.getConstructors()[0].newInstance(api);
            features.put(feature.getName(), feature);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            CrispyLogger.printException(api.getPlugin(), e, "Couldn't register the feature " + fClass.getSimpleName());
        }
    }

    public void load() throws ManagerLoadException {
        for (CrispyFeature<?, ?, ?, ?> feature : features.values()) {
            if (!feature.load())
                throw new ManagerLoadException("Couldn't load the feature " + feature.getName());
        }
    }

    public void unload() {
        for (CrispyFeature<?, ?, ?, ?> f : features.values()) {
            f.unload();
        }
    }

    public CrispyFeature<?, ?, ?, ?> getFeature(String name) {
        return features.get(name);
    }

    public <T extends CrispyFeature<?, ?, ?, ?>> T getFeature(Class<T> tClass) {
        for (CrispyFeature<?, ?, ?, ?> f : features.values()) {
            if(tClass.equals(f.getClass())) {
                return tClass.cast(f);
            }
        }
        return null;
    }

    public Set<CrispyFeature<?, ?, ?, ?>> getFeatures() {
        return new HashSet<>(features.values());
    }

    public Set<CrispyFeature<?, ?, ?, ?>> getEnabledFeatures() {
        return features.values().stream().filter(CrispyFeature::isEnabled).collect(Collectors.toSet());
    }

    @Override
    public void reload() throws ManagerReloadException {
        boolean success = true;
        Set<CrispyFeature<?, ?, ?, ?>> enabledFeatures = features.values().stream().filter(CrispyFeature::isEnabled).collect(Collectors.toSet());
        for (CrispyFeature<?, ?, ?, ?> f : enabledFeatures) {
            try {
                if(!f.reload() && success) {
                    success = false;
                }
            } catch (Exception e) {
                throw new ManagerReloadException(e, true, true);
            }
        }

        if(!success) {
            throw new ManagerReloadException("", true, false);
        }
    }
}

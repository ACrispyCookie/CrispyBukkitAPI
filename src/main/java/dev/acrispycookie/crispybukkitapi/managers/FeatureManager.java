package dev.acrispycookie.crispybukkitapi.managers;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.features.Feature;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class FeatureManager extends BaseManager {

    private final ArrayList<Feature> features;
    private final ArrayList<Class<? extends Feature>> toLoad;

    public FeatureManager(CrispyBukkitAPI api) {
        super(api);
        this.features = new ArrayList<>();
        this.toLoad = new ArrayList<>();
    }

    public void registerFeature(Class<? extends Feature> fClass) {
        toLoad.add(fClass);
    }

    public void load() {
        toLoad.forEach(fClass -> {
            try {
                features.add((Feature) fClass.getConstructors()[0].newInstance(api));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                api.getPlugin().getLogger().log(Level.SEVERE, "Couldn't create new instance of the feature: " + fClass.getName());
                throw new RuntimeException(e);
            }
        });
    }

    public <T extends Feature> T getFeature(Class<T> tClass) {
        for (Feature f : features) {
            if(tClass.equals(f.getClass())) {
                return tClass.cast(f);
            }
        }
        return null;
    }

    public int getEnabledFeatures() {
        int count = 0;
        for (Feature f : features) {
            if(f.isEnabled())
                count++;
        }
        return count;
    }

    @Override
    public boolean reload() {
        AtomicBoolean restart = new AtomicBoolean(false);
        features.forEach(f -> {
            if(f.reload() && !restart.get()) {
                restart.set(true);
            }
        });

        if(restart.get()) {
            api.getPlugin().getLogger().log(Level.WARNING, "RESTART REQUIRED! One or more features need restarting after reloading!");
        }
        return restart.get();
    }
}

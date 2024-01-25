package dev.acrispycookie.crispybukkitapi.managers;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.features.CrispyFeature;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class FeatureManager extends BaseManager {

    private final ArrayList<CrispyFeature> features;
    private final ArrayList<Class<? extends CrispyFeature>> toLoad;

    public FeatureManager(CrispyBukkitAPI api) {
        super(api);
        this.features = new ArrayList<>();
        this.toLoad = new ArrayList<>();
    }

    public void registerFeature(Class<? extends CrispyFeature> fClass) {
        toLoad.add(fClass);
    }

    public void load() throws ManagerLoadException {
        for (Class<? extends CrispyFeature> fClass : toLoad) {
            try {
                features.add((CrispyFeature) fClass.getConstructors()[0].newInstance(api));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new ManagerLoadException(e);
            }
        }
    }

    public void unload() {
        for (CrispyFeature f : features) {
            f.unload();
        }
    }

    public <T extends CrispyFeature> T getFeature(Class<T> tClass) {
        for (CrispyFeature f : features) {
            if(tClass.equals(f.getClass())) {
                return tClass.cast(f);
            }
        }
        return null;
    }

    public int getEnabledFeatures() {
        int count = 0;
        for (CrispyFeature f : features) {
            if(f.isEnabled())
                count++;
        }
        return count;
    }

    @Override
    public void reload() throws ManagerReloadException {
        boolean restart = false;
        for (CrispyFeature f : features) {
            try {
                if(f.reload() && !restart) {
                    restart = true;
                }
            } catch (Exception e) {
                throw new ManagerReloadException(e, true, true);
            }
        }

        if(restart) {
            throw new ManagerReloadException("", restart, false);
        }
    }
}

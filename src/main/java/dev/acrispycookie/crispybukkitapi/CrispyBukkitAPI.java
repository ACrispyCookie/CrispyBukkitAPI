package dev.acrispycookie.crispybukkitapi;

import dev.acrispycookie.crispybukkitapi.features.CrispyFeature;
import dev.acrispycookie.crispybukkitapi.features.base.BaseFeature;
import dev.acrispycookie.crispybukkitapi.managers.*;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.DatabaseSchema;
import dev.acrispycookie.crispycommons.CommonsSettings;
import dev.acrispycookie.crispycommons.CrispyCommons;
import dev.acrispycookie.crispycommons.utility.logging.CrispyLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

public final class CrispyBukkitAPI {

    private final JavaPlugin plugin;
    private long beforeLoading;
    private final HashMap<Class<? extends BaseManager>, BaseManager> managers;

    public CrispyBukkitAPI(JavaPlugin plugin, CommonsSettings settings) {
        this.plugin = plugin;
        this.managers = new HashMap<>();
        beforeLoading = System.currentTimeMillis();
        CrispyCommons.init(plugin, settings);
        initManagers();
        addFeature(BaseFeature.class);
    }

    public CrispyBukkitAPI disableConfig() {
        getManager(ConfigManager.class).disableDefault();
        return this;
    }

    public CrispyBukkitAPI disableLanguage() {
        getManager(LanguageManager.class).disableDefault();
        return this;
    }

    public CrispyBukkitAPI addConfig(ConfigManager.ConfigInfo info) {
        getManager(ConfigManager.class).addConfig(info);
        return this;
    }

    public CrispyBukkitAPI setDatabaseSchema(DatabaseSchema schema) {
        getManager(DataManager.class).setSchema(schema);
        return this;
    }

    public CrispyBukkitAPI addFeature(Class<? extends CrispyFeature<?, ?, ?>> feature) {
        getManager(FeatureManager.class).registerFeature(feature);
        return this;
    }

    public void start() {
        for (ManagerType t : ManagerType.values()) {
            try {
                managers.get(t.getType()).load();
            } catch (BaseManager.ManagerLoadException e) {
                CrispyLogger.printException(plugin, e, "Couldn't load because this manager failed to load: " + t.name());
                return;
            }
        }
        CrispyLogger.log(plugin, Level.INFO, "Loaded plugin with " + getManager(FeatureManager.class).getEnabledFeatures().size() + " features enabled! (" + (System.currentTimeMillis() - beforeLoading) + "ms)");
    }

    public void stop() {
        CrispyLogger.log(plugin, Level.INFO, "Goodbye!");
    }


    public <T extends BaseManager> T getManager(Class<T> type) {
        return type.cast(managers.get(type));
    }

    public boolean reload() {
        beforeLoading = System.currentTimeMillis();
        boolean success = true;
        for (ManagerType t : ManagerType.values()) {
            try {
                managers.get(t.getType()).reload();
            } catch (BaseManager.ManagerReloadException e) {
                if (e.stopLoading()) {
                    CrispyLogger.printException(plugin, e, "Couldn't reload because this manager failed to reload: " + t.name() + ".");
                    CrispyLogger.log(plugin, Level.SEVERE, "Fix it and restart the server.");
                    return false;
                }
                if (e.requiresRestart()) {
                    CrispyLogger.log(plugin, Level.WARNING, "This manager requires restarting: " + t.name());
                    if (success)
                        success = !e.requiresRestart();
                }
            }
        }
        if(!success)
            CrispyLogger.log(plugin, Level.WARNING, "RESTART REQUIRED! One or more features need restarting after reloading!");
        CrispyLogger.log(plugin, Level.INFO, "Finished reloading plugin with " + getManager(FeatureManager.class).getEnabledFeatures().size() + " features enabled! (" + (System.currentTimeMillis() - beforeLoading) + "ms)");
        return success;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    private void initManagers() {
        Arrays.stream(ManagerType.values()).forEach(m -> {
            try {
                managers.put(m.getType(), (BaseManager) m.getType().getConstructors()[0].newInstance(this));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // From the highest priority on reloading to the lowest
    public enum ManagerType {
        CONFIG(ConfigManager.class),
        LANGUAGE(LanguageManager.class),
        DATABASE(DataManager.class),
        FEATURE(FeatureManager.class);

        private final Class<? extends BaseManager> type;
        ManagerType(Class<? extends BaseManager> type) {
            this.type = type;
        }

        public Class<? extends BaseManager> getType() {
            return type;
        }
    }
}

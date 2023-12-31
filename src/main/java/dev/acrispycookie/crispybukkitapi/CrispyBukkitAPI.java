package dev.acrispycookie.crispybukkitapi;

import dev.acrispycookie.crispybukkitapi.features.CrispyFeature;
import dev.acrispycookie.crispybukkitapi.features.reload.ReloadFeature;
import dev.acrispycookie.crispybukkitapi.managers.*;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.DatabaseSchema;
import dev.acrispycookie.crispycommons.implementations.CrispyCommons;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public final class CrispyBukkitAPI {

    private final JavaPlugin plugin;
    private final long beforeLoading;
    private final HashMap<Class<? extends BaseManager>, BaseManager> managers;

    public CrispyBukkitAPI(JavaPlugin plugin) {
        this.plugin = plugin;
        this.managers = new HashMap<>();
        beforeLoading = System.currentTimeMillis();
        CrispyCommons.init(plugin);
        initManagers();
        addFeature(ReloadFeature.class);
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

    public CrispyBukkitAPI addFeature(Class<? extends CrispyFeature> feature) {
        getManager(FeatureManager.class).registerFeature(feature);
        return this;
    }

    public void start() {
        for (ManagerType t : ManagerType.values()) {
            try {
                managers.get(t.getType()).load();
            } catch (BaseManager.ManagerLoadException e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                plugin.getLogger().log(Level.SEVERE,
                        "Couldn't load because this manager failed to load: " + t.name());
                plugin.getLogger().log(Level.SEVERE,
                        "Reason: " + sw);
                return;
            }
        }
        plugin.getLogger().log(Level.INFO,
                "Loaded plugin with " + getManager(FeatureManager.class).getEnabledFeatures() + " features enabled! (" + (System.currentTimeMillis() - beforeLoading) + "ms)");
    }

    public void stop() {
        plugin.getLogger().log(Level.INFO, "Goodbye!");
    }


    public <T extends BaseManager> T getManager(Class<T> type) {
        return type.cast(managers.get(type));
    }

    public boolean reload() {
        AtomicBoolean restart = new AtomicBoolean(false);
        for (ManagerType t : ManagerType.values()) {
            try {
                managers.get(t.getType()).reload();
            } catch (BaseManager.ManagerReloadException e) {
                if (e.stopLoading()) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    getPlugin().getLogger().log(Level.SEVERE, "Couldn't reload because this manager failed to reload: " + t.name() + ".");
                    getPlugin().getLogger().log(Level.SEVERE,
                            "Reason: " + sw + "\n Fix it and restart the server.");
                    return false;
                }
                if (e.requiresRestart()) {
                    getPlugin().getLogger().log(Level.SEVERE, "This manager requires restarting: " + t.name());
                    if (!restart.get())
                        restart.set(e.requiresRestart());
                }
            }
        }
        if(restart.get())
            getPlugin().getLogger().log(Level.SEVERE, "RESTART REQUIRED! One or more features need restarting after reloading!");
        return restart.get();
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

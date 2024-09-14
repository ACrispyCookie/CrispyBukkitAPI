package dev.acrispycookie.crispypluginapi;

import dev.acrispycookie.crispycommons.CommonsSettings;
import dev.acrispycookie.crispycommons.CrispyCommons;
import dev.acrispycookie.crispycommons.logging.CrispyLogger;
import dev.acrispycookie.crispycommons.platform.CrispyPlugin;
import dev.acrispycookie.crispycommons.platform.commands.PlatformCommand;
import dev.acrispycookie.crispycommons.platform.commands.PlatformListener;
import dev.acrispycookie.crispypluginapi.features.CrispyFeature;
import dev.acrispycookie.crispypluginapi.managers.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

public abstract class CrispyPluginAPI {

    protected final CrispyPlugin plugin;
    protected final CrispyCommons commons;
    private long beforeLoading;
    private final HashMap<Class<? extends BaseManager>, BaseManager> managers;
    protected abstract CrispyCommons setupCrispyCommons(CommonsSettings settings);
    public abstract void registerListener(CrispyPlugin plugin, PlatformListener listener);
    public abstract void unregisterListener(PlatformListener listener);
    public abstract boolean isPluginEnabled(String name);

    public CrispyPluginAPI(CrispyPlugin plugin, CommonsSettings settings) {
        this.plugin = plugin;
        this.managers = new HashMap<>();
        this.beforeLoading = System.currentTimeMillis();
        this.commons = setupCrispyCommons(settings);
        initManagers();
    }

    public CrispyPluginAPI disableConfig() {
        getManager(ConfigManager.class).disableDefault();
        return this;
    }

    public CrispyPluginAPI disableLanguage() {
        getManager(LanguageManager.class).disableDefault();
        return this;
    }

    public CrispyPluginAPI enableDatabase() {
        getManager(DataManager.class).setEnabled(true);
        return this;
    }

    public CrispyPluginAPI addConfig(ConfigManager.ConfigInfo info) {
        getManager(ConfigManager.class).addConfig(info);
        return this;
    }

    public CrispyPluginAPI addFeature(Class<? extends CrispyFeature<?, ?, ?, ?>> feature) {
        getManager(FeatureManager.class).registerFeature(feature);
        return this;
    }

    public void start() {
        for (ManagerType t : ManagerType.values()) {
            try {
                managers.get(t.getType()).load();
            } catch (BaseManager.ManagerLoadException e) {
                CrispyLogger.printException(plugin, e, "Couldn't load because this manager failed to load: " + t.name());
                CrispyLogger.log(plugin, Level.SEVERE, "Configure your plugin correctly and restart or contact the developer!");
                return;
            } catch (Exception e) {
                CrispyLogger.printException(plugin, e, "A critical error occurred while loading the manager: " + t.name());
                CrispyLogger.log(plugin, Level.SEVERE, "Please contact your developer!");
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

    public CrispyPlugin getPlugin() {
        return plugin;
    }

    public CrispyCommons getCommons() {
        return commons;
    }

    public void registerCommand(CrispyPlugin plugin, String prefix, PlatformCommand command) {
        getCommons().registerCommand(plugin, prefix, command);
    }

    public void unregisterCommand(CrispyPlugin plugin, PlatformCommand command) {
        getCommons().unregisterCommand(plugin, command);
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

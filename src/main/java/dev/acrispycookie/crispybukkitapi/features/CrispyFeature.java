package dev.acrispycookie.crispybukkitapi.features;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.managers.ConfigManager;
import dev.acrispycookie.crispybukkitapi.managers.DataManager;
import dev.acrispycookie.crispybukkitapi.managers.LanguageManager;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.structure.AbstractSqlDatabase;
import dev.acrispycookie.crispycommons.CrispyCommons;
import dev.acrispycookie.crispycommons.utility.nms.CommandRegister;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.*;
import java.util.logging.Level;

public abstract class CrispyFeature {

    private boolean enabled;
    private boolean loaded = false;
    private final Set<CrispyFeatureCommand<?>> commands;
    private final Set<CrispyFeatureListener<?>> listeners;
    private static final Set<String> loadedDependencies = new HashSet<>();
    protected final CrispyBukkitAPI api;
    public abstract String getName();
    protected abstract void onLoad(Set<String> loadedDependencies);
    protected abstract boolean onReload();
    protected abstract void onUnload();
    protected abstract Set<CrispyFeatureCommand<? extends CrispyFeature>> commandsToLoad();
    protected abstract Set<String> getDependencies();
    protected abstract Set<CrispyFeatureListener<? extends CrispyFeature>> listenersToLoad();

    public CrispyFeature(CrispyBukkitAPI api) {
        this.api = api;
        this.enabled = get(new FeatureOptionInfo("enabled", ConfigManager.DataType.BOOLEAN), Boolean.class);
        this.commands = new HashSet<>();
        this.listeners = new HashSet<>();
        if (enabled) {
            load();
        }
    }

    private void loadCommands() {
        commandsToLoad().forEach(c -> {
            CommandRegister.newInstance().register(api.getPlugin(), api.getPlugin().getName(), c);
            commands.add(c);
        });
    }

    private void loadListeners() {
        listenersToLoad().forEach(c -> {
            Bukkit.getPluginManager().registerEvents(c, api.getPlugin());
            listeners.add(c);
        });
    }

    private Set<String> checkForDependencies() {
        Set<String> missing = new HashSet<>();
        for(String dep : getDependencies()) {
            if(!Bukkit.getPluginManager().isPluginEnabled(dep)) {
               missing.add(dep);
            }
        }
        return missing;
    }

    public boolean load() {
        if (loaded)
            return true;
        Set<String> missingDeps = checkForDependencies();
        if(!missingDeps.isEmpty()) {
            api.getPlugin().getLogger().log(Level.WARNING,
                    "Couldn't load the feature \"" + getName() + "\" because the following dependencies are missing: " + String.join(", ", missingDeps));
            return false;
        }
        onLoad(loadedDependencies);
        loadedDependencies.addAll(getDependencies());
        loadCommands();
        loadListeners();
        api.getPlugin().getLogger().log(Level.INFO,
                "Loaded feature \"" + getName() + "\"!");
        loaded = true;
        return true;
    }

    public void unload() {
        if (!loaded)
            return;
        commands.forEach(CrispyFeatureCommand::unregister);
        listeners.forEach(HandlerList::unregisterAll);
        commands.clear();
        listeners.clear();
        onUnload();
        api.getPlugin().getLogger().log(Level.INFO,
                "Unloaded feature \"" + getName() + "\"!");
        loaded = false;
    }

    public boolean reload() {
        boolean newEnabled = get(new FeatureOptionInfo("enabled", ConfigManager.DataType.BOOLEAN), Boolean.class);
        if (!newEnabled) {
            if (enabled)
                unload();
            enabled = false;
            return true;
        }

        if (!enabled) {
            enabled = true;
            return load();
        }

        unload();
        if (onReload()) {
            loadCommands();
            loadListeners();
            api.getPlugin().getLogger().log(Level.INFO, "Feature \"" + getName() + "\" was reloaded successfully");
            loaded = true;
            return true;
        } else {
            unload();
            api.getPlugin().getLogger().log(Level.INFO, "Feature \"" + getName() + "\" needs a restart to be enabled again!");
            return false;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        set(new FeatureOptionInfo("enabled", ConfigManager.DataType.BOOLEAN), Boolean.class, enabled);
    }

    public <T> T get(FeatureOptionInfo info, Class<T> tClass) {
        return new FeatureOption(info).get(tClass);
    }

    public <T> void set(FeatureOptionInfo info, Class<T> tClass, T value) {
        new FeatureOption(info).set(tClass, value);
    }

    public String getPerm(String path) {
        return new FeaturePermission(path).get();
    }

    public FeatureMessage getMsg(String path) {
        return new FeatureMessage(path);
    }

    public AbstractSqlDatabase getDb() { return api.getManager(DataManager.class).getDatabase(); }

    public Set<CrispyFeatureCommand<?>> getCommands() {
        return commands;
    }

    public Set<CrispyFeatureListener<?>> getListeners() {
        return listeners;
    }

    public static Set<String> getLoadedDependencies() {
        return loadedDependencies;
    }

    public class FeatureMessage {
        private final String path;
        public FeatureMessage(String path) {
            this.path = path;
        }

        public Component get() {
            return api.getManager(LanguageManager.class).get(getName() + "." + path);
        }

        public Component get(HashMap<String, String> placeholders) {
            return api.getManager(LanguageManager.class).get(getName() + "." + path, placeholders);
        }

        public void send(CommandSender recipient) {
            Audience au = CrispyCommons.getBukkitAudiences().sender(recipient);
            au.sendMessage(this::get);
        }

        public void send(Player recipient) {
            send((CommandSender) recipient);
        }

        public void send(CommandSender recipient, HashMap<String, String> placeholders) {
            Audience au = CrispyCommons.getBukkitAudiences().sender(recipient);
            au.sendMessage(get(placeholders));
        }

        public void send(Player recipient, HashMap<String, String> placeholders) {
            send((CommandSender) recipient, placeholders);
        }

    }

    protected class FeaturePermission {
        private final String path;
        public FeaturePermission(String path) {
            this.path = path;
        }

        public String get() {
            return api.getManager(ConfigManager.class).getFromType(
                    api.getManager(ConfigManager.class).getDefault(),"features." + getName() + ".permissions." + path,
                    ConfigManager.DataType.STRING, String.class
            );
        }

    }

    protected class FeatureOption {
        private final FeatureOptionInfo info;
        public FeatureOption(FeatureOptionInfo info) {
            this.info = info;
        }

        public <T> T get(Class<T> type) {
            return type.cast(api.getManager(ConfigManager.class).getFromType(
                    api.getManager(ConfigManager.class).getDefault(),
                    "features." + getName() + "." + info.getPath(),
                    info.getType(),
                    type
            ));
        }

        public <T> void set(Class<T> type, T value) {
            api.getManager(ConfigManager.class).save(
                    api.getManager(ConfigManager.class).getDefault(),
                    "features." + getName() + "." + info.getPath(),
                    value
            );
        }

    }

    public static class FeatureOptionInfo {
        private final String path;
        private final ConfigManager.DataType type;
        public FeatureOptionInfo(String path, ConfigManager.DataType type) {
            this.path = path;
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public ConfigManager.DataType getType() {
            return type;
        }
    }
}

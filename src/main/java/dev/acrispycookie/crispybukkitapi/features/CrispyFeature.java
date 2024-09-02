package dev.acrispycookie.crispybukkitapi.features;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.features.options.DataOption;
import dev.acrispycookie.crispybukkitapi.features.options.PathOption;
import dev.acrispycookie.crispybukkitapi.managers.ConfigManager;
import dev.acrispycookie.crispybukkitapi.managers.LanguageManager;
import dev.acrispycookie.crispybukkitapi.utility.DataType;
import dev.acrispycookie.crispycommons.CrispyCommons;
import dev.acrispycookie.crispycommons.utility.nms.CommandRegister;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public abstract class CrispyFeature<C extends DataOption, M extends PathOption, P extends PathOption> {

    private boolean enabled;
    private boolean loaded = false;
    private final Set<CrispyFeatureCommand<?>> commands;
    private final Set<CrispyFeatureListener<?>> listeners;
    private static final Set<String> loadedDependencies = new HashSet<>();
    protected final CrispyBukkitAPI api;
    public abstract String getName();
    protected abstract void onLoad();
    protected abstract boolean onReload();
    protected abstract void onUnload();
    protected abstract Set<C> getOptions();
    protected abstract Set<M> getMessages();
    protected abstract Set<P> getPermissions();
    protected abstract Set<String> getDependencies();
    protected abstract Set<CrispyFeatureCommand<?>> commandsToLoad();
    protected abstract Set<CrispyFeatureListener<?>> listenersToLoad();

    public CrispyFeature(CrispyBukkitAPI api) {
        this.api = api;
        this.enabled = get("enabled", DataType.BOOLEAN, Boolean.class);
        this.commands = new HashSet<>();
        this.listeners = new HashSet<>();
        if (enabled) {
            load();
        }
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
        onLoad();
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
        boolean newEnabled = get("enabled", DataType.BOOLEAN, Boolean.class);
        if (!newEnabled) {
            if (enabled)
                unload();
            setEnabled(false);
            return true;
        }

        if (!enabled) {
            setEnabled(true);
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

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        set("enabled", enabled);
    }

    public <T> T get(C option, Class<T> tClass) {
        return get(option.path(), option.type(), tClass);
    }

    public void set(C option, Object value) {
        set(option.path(), value);
    }

    public String getPerm(P option) {
        return api.getManager(ConfigManager.class).getFromType(
                api.getManager(ConfigManager.class).getDefault(),"features." + getName() + ".permissions." + option.path(),
                DataType.STRING, String.class
        );
    }

    public FeatureMessage getMsg(M option) {
        return new FeatureMessage(option.path());
    }

    public Set<CrispyFeatureCommand<?>> getCommands() {
        return commands;
    }

    public Set<CrispyFeatureListener<?>> getListeners() {
        return listeners;
    }

    public static Set<String> getLoadedDependencies() {
        return loadedDependencies;
    }

    private <T> T get(String path, DataType type, Class<T> tClass) {
        return tClass.cast(api.getManager(ConfigManager.class).getFromType(
                api.getManager(ConfigManager.class).getDefault(),
                "features." + getName() + "." + path,
                type,
                tClass
        ));
    }

    private void set(String path, Object value) {
        api.getManager(ConfigManager.class).save(
                api.getManager(ConfigManager.class).getDefault(),
                "features." + getName() + "." + path,
                value
        );
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
}

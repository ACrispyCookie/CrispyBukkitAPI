package dev.acrispycookie.crispybukkitapi.features;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.managers.ConfigManager;
import dev.acrispycookie.crispybukkitapi.managers.DataManager;
import dev.acrispycookie.crispybukkitapi.managers.LanguageManager;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.structure.AbstractSqlDatabase;
import dev.acrispycookie.crispycommons.nms.wrappers.utilities.CommandRegister;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.*;
import java.util.logging.Level;

public abstract class CrispyFeature {

    private boolean enabled;
    private final ArrayList<CrispyFeatureCommand<?>> commands;
    private final ArrayList<CrispyFeatureListener<?>> listeners;
    private static final Set<String> loadedDependencies = new HashSet<>();
    protected final CrispyBukkitAPI api;
    public abstract String getName();
    protected abstract void onLoad(Set<String> loadedDependencies);
    protected abstract boolean onReload();
    protected abstract void onUnload();
    protected abstract List<CrispyFeatureCommand<? extends CrispyFeature>> commandsToLoad();
    protected abstract List<String> getDependencies();
    protected abstract List<CrispyFeatureListener<? extends CrispyFeature>> listenersToLoad();

    public CrispyFeature(CrispyBukkitAPI api) {
        this.api = api;
        this.enabled = get(new FeatureOptionInfo("enabled", ConfigManager.DataType.BOOLEAN), Boolean.class);
        this.commands = new ArrayList<>();
        this.listeners = new ArrayList<>();
        if (enabled) {
            ArrayList<String> missingDeps = checkForDependencies();
            if(!missingDeps.isEmpty()) {
                api.getPlugin().getLogger().log(Level.WARNING,
                        "Couldn't load the feature \"" + getName() + "\" because the following dependencies are missing: " + String.join(", ", missingDeps));
                return;
            }
            onLoad(loadedDependencies);
            loadedDependencies.addAll(getDependencies());
            loadCommands();
            loadListeners();
            api.getPlugin().getLogger().log(Level.INFO,
                    "Loaded feature \"" + getName() + "\"!");
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

    private ArrayList<String> checkForDependencies() {
        ArrayList<String> missing = new ArrayList<>();
        for(String dep : getDependencies()) {
            if(!Bukkit.getPluginManager().isPluginEnabled(dep)) {
               missing.add(dep);
            }
        }
        return missing;
    }

    public void unload() {
        commands.forEach(CrispyFeatureCommand::unregister);
        listeners.forEach(HandlerList::unregisterAll);
        commands.clear();
        listeners.clear();
        onUnload();
    }

    public boolean reload() {
        this.enabled = get(new FeatureOptionInfo("enabled", ConfigManager.DataType.BOOLEAN), Boolean.class);
        if(!enabled) {
            unload();
            return false;
        }

        unload();
        if (onReload()) {
            loadCommands();
            loadListeners();
            api.getPlugin().getLogger().log(Level.INFO, "Feature \"" + getName() + "\" was reloaded successfully");
            return false;
        } else {
            unload();
            api.getPlugin().getLogger().log(Level.INFO, "Feature \"" + getName() + "\" needs a restart to be enabled again!");
            return true;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public <T> T get(FeatureOptionInfo info, Class<T> tClass) {
        return new FeatureOption(info).get(tClass);
    }

    public String getPerm(String path) {
        return new FeaturePermission(path).get();
    }

    public FeatureMessage getMsg(String path) {
        return new FeatureMessage(path);
    }

    public AbstractSqlDatabase getDb() { return api.getManager(DataManager.class).getDatabase(); }

    public ArrayList<CrispyFeatureCommand<?>> getCommands() {
        return commands;
    }

    public ArrayList<CrispyFeatureListener<?>> getListeners() {
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

        public TextComponent get() {
            return api.getManager(LanguageManager.class).get(getName() + "." + path);
        }

        public TextComponent get(HashMap<String, String> placeholders) {
            return api.getManager(LanguageManager.class).get(getName() + "." + path, placeholders);
        }

        public void send(CommandSender recipient) {
            recipient.sendMessage(get().getText());
        }

        public void send(Player recipient) {
            recipient.spigot().sendMessage(get());
        }

        public void send(CommandSender recipient, HashMap<String, String> placeholders) {
            recipient.sendMessage(get(placeholders).getText());
        }

        public void send(Player recipient, HashMap<String, String> placeholders) {
            recipient.spigot().sendMessage(get(placeholders));
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
                    api.getManager(ConfigManager.class).getDefault(),"features." + getName() + "." + info.getPath(),
                    info.getType(),
                    type
            ));
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

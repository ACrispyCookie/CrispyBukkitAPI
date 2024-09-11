package dev.acrispycookie.crispybukkitapi.features;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.features.options.DataOption;
import dev.acrispycookie.crispybukkitapi.features.options.PersistentOption;
import dev.acrispycookie.crispybukkitapi.features.options.StringOption;
import dev.acrispycookie.crispybukkitapi.managers.ConfigManager;
import dev.acrispycookie.crispybukkitapi.managers.DataManager;
import dev.acrispycookie.crispybukkitapi.managers.LanguageManager;
import dev.acrispycookie.crispybukkitapi.utility.DataType;
import dev.acrispycookie.crispycommons.CrispyCommons;
import dev.acrispycookie.crispycommons.utility.logging.CrispyLogger;
import dev.acrispycookie.crispycommons.utility.nms.CommandRegister;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public abstract class CrispyFeature<C extends DataOption, M extends StringOption, P extends StringOption, D extends PersistentOption> {

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
    public abstract Set<D> getData();
    protected abstract Set<C> getOptions();
    protected abstract Set<M> getMessages();
    protected abstract Set<P> getPermissions();
    protected abstract Set<String> getDependencies();
    protected abstract Set<CrispyFeatureCommand<?>> commandsToLoad();
    protected abstract Set<CrispyFeatureListener<?>> listenersToLoad();

    public CrispyFeature(CrispyBukkitAPI api) {
        this.api = api;
        this.enabled = getOption("enabled", DataType.BOOLEAN, Boolean.class);
        this.commands = new HashSet<>();
        this.listeners = new HashSet<>();
    }

    public boolean load() {
        if (!enabled)
            return true;
        if (loaded)
            return true;
        Set<String> missingDeps = checkForDependencies();
        if(!missingDeps.isEmpty()) {
            CrispyLogger.log(api.getPlugin(), Level.WARNING,
                    "Couldn't load the feature \"" + getName() + "\" because the following dependencies are missing: " + String.join(", ", missingDeps));
            return false;
        }
        onLoad();
        loadedDependencies.addAll(getDependencies());
        loadCommands();
        loadListeners();
        CrispyLogger.log(api.getPlugin(), Level.INFO,
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
        CrispyLogger.log(api.getPlugin(), Level.INFO,
                "Unloaded feature \"" + getName() + "\"!");
        loaded = false;
    }

    public boolean reload() {
        boolean newEnabled = getOption("enabled", DataType.BOOLEAN, Boolean.class);
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
            CrispyLogger.log(api.getPlugin(), Level.INFO, "Feature \"" + getName() + "\" was reloaded successfully");
            loaded = true;
            return true;
        } else {
            unload();
            CrispyLogger.log(api.getPlugin(), Level.INFO, "Feature \"" + getName() + "\" needs a restart to be enabled again!");
            return false;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setOption("enabled", enabled);
    }

    public <T> T getOption(C option, Class<T> tClass) {
        return getOption(option.path(), option.type(), tClass);
    }

    public void setOption(C option, Object value) {
        setOption(option.path(), value);
    }

    public String getPermission(P option) {
        return api.getManager(ConfigManager.class).getFromType(
                api.getManager(ConfigManager.class).getDefault(),"features." + getName() + ".permissions." + option.path(),
                DataType.STRING, String.class
        );
    }

    public FeatureMessage getMessage(M option) {
        return new FeatureMessage(option.path());
    }

    public Session getNewDataSession() {
        return api.getManager(DataManager.class).newSession();
    }

    public Object getData(D option, Object id) {
        return getData(option.clazz(), id);
    }

    public <T> T getData(Class<T> clazz, Object id) {
        DataManager manager = api.getManager(DataManager.class);
        Transaction transaction = null;
        try (Session session = manager.newSession()) {
            transaction = session.beginTransaction();
            T toReturn = session.get(clazz, id);
            transaction.commit();
            return toReturn;
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            throw new RuntimeException(e);
        }
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

    private <T> T getOption(String path, DataType type, Class<T> tClass) {
        return tClass.cast(api.getManager(ConfigManager.class).getFromType(
                api.getManager(ConfigManager.class).getDefault(),
                "features." + getName() + "." + path,
                type,
                tClass
        ));
    }

    private void setOption(String path, Object value) {
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

package dev.acrispycookie.crispypluginapi.spigot.features.base;

import com.google.common.collect.Sets;
import dev.acrispycookie.crispypluginapi.features.options.ConfigurationOption;
import dev.acrispycookie.crispypluginapi.features.options.PersistentOption;
import dev.acrispycookie.crispypluginapi.features.options.StringOption;
import dev.acrispycookie.crispypluginapi.spigot.SpigotPluginAPI;
import dev.acrispycookie.crispypluginapi.spigot.features.SpigotFeature;
import dev.acrispycookie.crispypluginapi.spigot.features.SpigotFeatureCommand;
import dev.acrispycookie.crispypluginapi.spigot.features.SpigotFeatureListener;
import dev.acrispycookie.crispypluginapi.spigot.features.base.commands.BaseCommand;
import dev.acrispycookie.crispypluginapi.utility.AdapterPair;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BaseFeature extends SpigotFeature<BaseFeature.Option, BaseFeature.Message, BaseFeature.Permission, BaseFeature.Data> {

    public BaseFeature(SpigotPluginAPI api) {
        super(api);
    }

    @Override
    public String getName() {
        return "base";
    }

    @Override
    protected void onLoad() {

    }

    @Override
    protected boolean onReload() {
        return true;
    }

    @Override
    protected void onUnload() {

    }

    @Override
    protected Set<SpigotFeatureCommand<?>> commandsToLoad() {
        return Collections.singleton(new BaseCommand(this, getApi()));
    }

    @Override
    protected Set<SpigotFeatureListener<?>> listenersToLoad() {
        return new HashSet<>();
    }

    @Override
    protected Set<AdapterPair<?>> serializableToRegister() {
        return new HashSet<>();
    }

    @Override
    protected Set<String> getDependencies() {
        return new HashSet<>();
    }

    @Override
    protected Set<Option> getOptions() {
        return Sets.newHashSet(Option.values());
    }

    @Override
    protected Set<Message> getMessages() {
        return Sets.newHashSet(Message.values());
    }

    @Override
    protected Set<Permission> getPermissions() {
        return Sets.newHashSet(Permission.values());
    }

    @Override
    public Set<Data> getData() {
        return Sets.newHashSet(Data.values());
    }

    public enum Data implements PersistentOption {
        ;

        private final Class<?> clazz;
        Data(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Class<?> clazz() {
            return clazz;
        }
    }

    public enum Option implements ConfigurationOption {
        ENABLED("enabled", Boolean.class);

        private final String path;
        private final Class<?> type;
        <T> Option(String path, Class<T> type) {
            this.path = path;
            this.type = type;
        }

        @Override
        public String path() {
            return path;
        }

        @Override
        public Class<?> clazz() {
            return type;
        }
    }

    public enum Message implements StringOption {
        RELOADED("reloaded"),
        FEATURE_ENABLED("feature-enabled"),
        FEATURE_ALREADY_ENABLED("feature-already-enabled"),
        FEATURE_DISABLED("feature-disabled"),
        FEATURE_ALREADY_DISABLED("feature-already-disabled"),
        FEATURE_RELOADED("feature-reloaded"),
        INVALID_FEATURE("invalid-feature"),
        USAGE("usage"),
        RESTART_REQUIRED("restart-required"),
        ERROR_OCCURRED("error-occurred"),
        NO_PERMISSION("no-permission");

        private final String path;
        Message(String path) {
            this.path = path;
        }

        public String path() {
            return path;
        }
    }

    public enum Permission implements StringOption {
        RELOAD("reload");

        private final String path;
        Permission(String path) {
            this.path = path;
        }

        public String path() {
            return path;
        }
    }
}

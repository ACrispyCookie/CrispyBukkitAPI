package dev.acrispycookie.crispybukkitapi.features.base;

import com.google.common.collect.Sets;
import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.features.CrispyFeature;
import dev.acrispycookie.crispybukkitapi.features.CrispyFeatureCommand;
import dev.acrispycookie.crispybukkitapi.features.CrispyFeatureListener;
import dev.acrispycookie.crispybukkitapi.features.base.commands.BaseCommand;
import dev.acrispycookie.crispybukkitapi.features.options.DataOption;
import dev.acrispycookie.crispybukkitapi.features.options.StringOption;
import dev.acrispycookie.crispybukkitapi.features.options.PersistentOption;
import dev.acrispycookie.crispybukkitapi.utility.DataType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BaseFeature extends CrispyFeature<BaseFeature.Option, BaseFeature.Message, BaseFeature.Permission, BaseFeature.Data> {

    public BaseFeature(CrispyBukkitAPI api) {
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
    protected Set<CrispyFeatureCommand<?>> commandsToLoad() {
        return Collections.singleton(new BaseCommand(this, api));
    }

    @Override
    protected Set<String> getDependencies() {
        return new HashSet<>();
    }

    @Override
    protected Set<CrispyFeatureListener<?>> listenersToLoad() {
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

    public enum Option implements DataOption {
        ENABLED("enabled", DataType.BOOLEAN);

        private final String path;
        private final DataType type;
        Option(String path, DataType type) {
            this.path = path;
            this.type = type;
        }

        @Override
        public String path() {
            return path;
        }

        @Override
        public DataType type() {
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

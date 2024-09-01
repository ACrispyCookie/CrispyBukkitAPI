package dev.acrispycookie.crispybukkitapi.features.reload;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.features.CrispyFeature;
import dev.acrispycookie.crispybukkitapi.features.CrispyFeatureCommand;
import dev.acrispycookie.crispybukkitapi.features.CrispyFeatureListener;
import dev.acrispycookie.crispybukkitapi.features.reload.commands.BaseCommand;

import java.util.*;

public class BaseFeature extends CrispyFeature {

    public BaseFeature(CrispyBukkitAPI api) {
        super(api);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    protected void onLoad(Set<String> loadedDependencies) {

    }

    @Override
    protected boolean onReload() {
        return true;
    }

    @Override
    protected void onUnload() {

    }

    @Override
    protected Set<CrispyFeatureCommand<? extends CrispyFeature>> commandsToLoad() {
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

    public enum MessageMap {
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
        MessageMap(String path) {
            this.path = path;
        }

        public String path() {
            return path;
        }
    }

    public enum PermissionMap {
        RELOAD("reload");

        private final String path;
        PermissionMap(String path) {
            this.path = path;
        }

        public String path() {
            return path;
        }
    }
}

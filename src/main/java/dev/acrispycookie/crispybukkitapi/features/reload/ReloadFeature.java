package dev.acrispycookie.crispybukkitapi.features.reload;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.features.CrispyFeature;
import dev.acrispycookie.crispybukkitapi.features.CrispyFeatureCommand;
import dev.acrispycookie.crispybukkitapi.features.CrispyFeatureListener;
import dev.acrispycookie.crispybukkitapi.features.reload.commands.ReloadCommand;

import java.util.*;

public class ReloadFeature extends CrispyFeature {

    public ReloadFeature(CrispyBukkitAPI api) {
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
    protected List<CrispyFeatureCommand<? extends CrispyFeature>> commandsToLoad() {
        return Collections.singletonList(new ReloadCommand(this, api));
    }

    @Override
    protected List<String> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    protected List<CrispyFeatureListener<?>> listenersToLoad() {
        return new ArrayList<>();
    }

    public enum MessageMap {
        RELOADED("reloaded"),
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

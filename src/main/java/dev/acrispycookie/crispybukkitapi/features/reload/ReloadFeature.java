package dev.acrispycookie.crispybukkitapi.features.reload;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.features.Feature;
import dev.acrispycookie.crispybukkitapi.features.FeatureCommand;
import dev.acrispycookie.crispybukkitapi.features.FeatureListener;
import dev.acrispycookie.crispybukkitapi.features.reload.commands.ReloadCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ReloadFeature extends Feature {

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
    protected List<FeatureCommand<? extends Feature>> commandsToLoad() {
        return Arrays.asList(new ReloadCommand(this, api));
    }

    @Override
    protected List<String> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    protected List<FeatureListener<?>> listenersToLoad() {
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

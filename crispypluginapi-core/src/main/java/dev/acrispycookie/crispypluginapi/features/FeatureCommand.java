package dev.acrispycookie.crispypluginapi.features;

import dev.acrispycookie.crispycommons.platform.player.PlatformCommandSender;
import dev.acrispycookie.crispycommons.platform.player.PlatformPlayer;
import dev.acrispycookie.crispypluginapi.CrispyPluginAPI;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class FeatureCommand<T extends CrispyFeature<?, ?, ?, ?>> {

    private final CrispyPluginAPI api;
    private final String name;
    private final String description;
    protected final T feature;
    protected abstract boolean runPlayer(PlatformPlayer player, String[] args);
    protected abstract boolean runConsole(PlatformCommandSender sender, String[] args);
    protected abstract List<String> getTabOptions(PlatformPlayer sender, String[] args);
    protected abstract void register();
    protected abstract void unregister();

    public FeatureCommand(T feature, CrispyPluginAPI api, String name, String description) {
        this.feature = feature;
        this.api = api;
        this.name = name;
        this.description = description;
    }

    public boolean execute(@NotNull PlatformCommandSender sender, @NotNull String usage, String[] args) {
        if (sender instanceof PlatformPlayer) {
            return runPlayer((PlatformPlayer) sender, args);
        }
        else {
            return runConsole(sender, args);
        }
    }

    public @NotNull List<String> tabComplete(@NotNull PlatformCommandSender sender, @NotNull String alias, String[] args) {
        if (sender instanceof PlatformPlayer) {
            return getTabOptions((PlatformPlayer) sender, args);
        }
        return new ArrayList<>();
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull String getDescription() {
        return description;
    }

    public CrispyPluginAPI getApi() {
        return api;
    }
}

package dev.acrispycookie.crispypluginapi.spigot.features.base.commands;

import com.google.common.collect.Sets;
import dev.acrispycookie.crispycommons.logging.CrispyLogger;
import dev.acrispycookie.crispypluginapi.features.CrispyFeature;
import dev.acrispycookie.crispypluginapi.managers.FeatureManager;
import dev.acrispycookie.crispypluginapi.spigot.SpigotPluginAPI;
import dev.acrispycookie.crispypluginapi.spigot.features.SpigotFeatureCommand;
import dev.acrispycookie.crispypluginapi.spigot.features.base.BaseFeature;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BaseCommand extends SpigotFeatureCommand<BaseFeature> {

    public BaseCommand(BaseFeature feature, SpigotPluginAPI api) {
        super(feature, api, api.getPlugin().getName(), "Reloads the plugin configuration and data.");
    }

    @Override
    protected boolean runPlayer(Player player, String[] args) {
        if(!player.hasPermission(feature.getPermission(BaseFeature.Permission.RELOAD))) {
            feature.getMessage(BaseFeature.Message.NO_PERMISSION).send(player);
            return false;
        }

        return runDefault(player, args);
    }

    @Override
    protected boolean runConsole(CommandSender sender, String[] args) {
        return runDefault(sender, args);
    }

    protected boolean runImplemented(CommandSender sender, String[] args) {
        feature.getMessage(BaseFeature.Message.USAGE).send(sender, new HashMap<String, String>() {{
            put("%name%", getApi().getPlugin().getName().toLowerCase());
        }});
        return false;
    }

    private boolean runDefault(CommandSender sender, String[] args) {
        if(args.length == 0) {
            return runImplemented(sender, args);
        }

        String action = args[0];
        if (action.equalsIgnoreCase("reload"))
            return runReload(sender, args);
        else if (action.equalsIgnoreCase("enable"))
            return runSpecific(sender, args, Action.ENABLE);
        else if (action.equalsIgnoreCase("disable"))
            return runSpecific(sender, args, Action.DISABLE);
        return runImplemented(sender, args);
    }

    private boolean runSpecific(CommandSender sender, String[] args, Action action) {
        if (args.length != 2) {
            feature.getMessage(BaseFeature.Message.USAGE).send(sender, new HashMap<String, String>() {{
                put("%name%", getApi().getPlugin().getName().toLowerCase());
            }});
            return false;
        }

        FeatureManager manager = getApi().getManager(FeatureManager.class);
        CrispyFeature<?, ?, ?, ?> feature = manager.getFeature(args[1]);
        if (feature == null) {
            this.feature.getMessage(BaseFeature.Message.INVALID_FEATURE).send(sender, new HashMap<String, String>() {{
                put("%features%", manager.getEnabledFeatures().stream().map(CrispyFeature::getName).collect(Collectors.joining(", ")));
            }});
            return false;
        }

        if (action == Action.ENABLE)
            enableSafe(sender, feature);
        else if (action == Action.DISABLE)
            disableSafe(sender, feature);
        else
            reloadSafe(sender, feature);
        return true;
    }

    private boolean runReload(CommandSender sender, String[] args) {
        if (args.length == 1) {
            boolean restartRequired = !getApi().reload();
            feature.getMessage(BaseFeature.Message.RELOADED).send(sender);
            if(restartRequired)
                feature.getMessage(BaseFeature.Message.RESTART_REQUIRED).send(sender);
            return true;
        }

        return runSpecific(sender, args, Action.RELOAD);
    }

    private void enableSafe(CommandSender sender, CrispyFeature<?, ?, ?, ?> toEnable) {
        if (toEnable.isEnabled()) {
            feature.getMessage(BaseFeature.Message.FEATURE_ALREADY_ENABLED).send(sender, new HashMap<String, String>() {{
                put("%name%", toEnable.getName());
            }});
            return;
        }

        try {
            toEnable.setEnabled(true);
            toEnable.load();
            feature.getMessage(BaseFeature.Message.FEATURE_ENABLED).send(sender, new HashMap<String, String>() {{
                put("%name%", toEnable.getName());
            }});
        } catch (Exception e) {
            CrispyLogger.printException(getApi().getPlugin(), e, "Error occurred while trying to enable the feature " + toEnable.getName());
            feature.getMessage(BaseFeature.Message.ERROR_OCCURRED).send(sender, new HashMap<String, String>() {{
                put("%action%", "enable");
            }});
        }
    }

    private void disableSafe(CommandSender sender, CrispyFeature<?, ?, ?, ?> toDisable) {
        if (!toDisable.isEnabled()) {
            feature.getMessage(BaseFeature.Message.FEATURE_ALREADY_DISABLED).send(sender, new HashMap<String, String>() {{
                put("%name%", toDisable.getName());
            }});
            return;
        }

        try {
            toDisable.setEnabled(false);
            toDisable.unload();
            feature.getMessage(BaseFeature.Message.FEATURE_DISABLED).send(sender, new HashMap<String, String>() {{
                put("%name%", toDisable.getName());
            }});
        } catch (Exception e) {
            CrispyLogger.printException(getApi().getPlugin(), e, "Error occurred while trying to disable the feature " + toDisable.getName());
            feature.getMessage(BaseFeature.Message.ERROR_OCCURRED).send(sender, new HashMap<String, String>() {{
                put("%action%", "disable");
            }});
        }
    }

    private void reloadSafe(CommandSender sender, CrispyFeature<?, ?, ?, ?> toReload) {
        if (!toReload.isEnabled()) {
            enableSafe(sender, toReload);
            return;
        }

        try {
            boolean restartRequired = !toReload.reload();
            feature.getMessage(BaseFeature.Message.FEATURE_RELOADED).send(sender, new HashMap<String, String>() {{
                put("%name%", toReload.getName());
            }});
            if (restartRequired)
                feature.getMessage(BaseFeature.Message.RESTART_REQUIRED).send(sender);
        } catch (Exception e) {
            CrispyLogger.printException(getApi().getPlugin(), e, "Error occurred while trying to reload the feature " + toReload.getName());
            feature.getMessage(BaseFeature.Message.ERROR_OCCURRED).send(sender, new HashMap<String, String>() {{
                put("%action%", "reload");
            }});
        }
    }

    @Override
    protected List<String> getTabOptions(Player sender, String[] args) {
        ArrayList<String> options = new ArrayList<>();
        if(args.length == 1)
            options.addAll(Sets.newHashSet("reload", "enable", "disable").stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toSet()));
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("enable")) {
                options.addAll(getApi().getManager(FeatureManager.class).getFeatures().stream()
                        .filter(f -> !f.isEnabled())
                        .map(CrispyFeature::getName)
                        .filter(s -> s.startsWith(args[1]))
                        .collect(Collectors.toList()));
            } else if (args[0].equalsIgnoreCase("disable")) {
                options.addAll(getApi().getManager(FeatureManager.class).getFeatures().stream()
                        .filter(CrispyFeature::isEnabled)
                        .map(CrispyFeature::getName)
                        .filter(s -> s.startsWith(args[1]))
                        .collect(Collectors.toList()));
            } else if (args[0].equalsIgnoreCase("reload")) {
                options.addAll(getApi().getManager(FeatureManager.class).getFeatures().stream()
                        .map(CrispyFeature::getName)
                        .filter(s -> s.startsWith(args[1]))
                        .collect(Collectors.toList()));
            }
        }
        return options;
    }

    enum Action {
        ENABLE,
        DISABLE,
        RELOAD
    }
}

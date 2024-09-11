package dev.acrispycookie.crispybukkitapi.features.base.commands;

import com.google.common.collect.Sets;
import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.features.CrispyFeature;
import dev.acrispycookie.crispybukkitapi.features.CrispyFeatureCommand;
import dev.acrispycookie.crispybukkitapi.features.base.BaseFeature;
import dev.acrispycookie.crispybukkitapi.managers.FeatureManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BaseCommand extends CrispyFeatureCommand<BaseFeature> {

    public BaseCommand(BaseFeature feature, CrispyBukkitAPI api) {
        super(feature, api, api.getPlugin().getName(), "Reloads the plugin configuration and data.");
    }

    @Override
    protected boolean runPlayer(Player player, String[] args) {
        if(!player.hasPermission(feature.getPerm(BaseFeature.Permission.RELOAD))) {
            feature.getMsg(BaseFeature.Message.NO_PERMISSION).send(player);
            return false;
        }

        return runDefault(player, args);
    }

    @Override
    protected boolean runConsole(CommandSender sender, String[] args) {
        return runDefault(sender, args);
    }

    protected boolean runImplemented(CommandSender sender, String[] args) {
        feature.getMsg(BaseFeature.Message.USAGE).send(sender, new HashMap<String, String>() {{
            put("%name%", api.getPlugin().getName());
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
            feature.getMsg(BaseFeature.Message.USAGE).send(sender, new HashMap<String, String>() {{
                put("%name%", api.getPlugin().getName());
            }});
            return false;
        }

        FeatureManager manager = api.getManager(FeatureManager.class);
        CrispyFeature<?, ?, ?, ?> feature = manager.getFeature(args[1]);
        if (feature == null) {
            this.feature.getMsg(BaseFeature.Message.INVALID_FEATURE).send(sender, new HashMap<String, String>() {{
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
            boolean restartRequired = !api.reload();
            feature.getMsg(BaseFeature.Message.RELOADED).send(sender);
            if(restartRequired)
                feature.getMsg(BaseFeature.Message.RESTART_REQUIRED).send(sender);
            return true;
        }

        return runSpecific(sender, args, Action.RELOAD);
    }

    private void enableSafe(CommandSender sender, CrispyFeature<?, ?, ?, ?> toEnable) {
        if (toEnable.isEnabled()) {
            feature.getMsg(BaseFeature.Message.FEATURE_ALREADY_ENABLED).send(sender, new HashMap<String, String>() {{
                put("%name%", toEnable.getName());
            }});
            return;
        }

        toEnable.setEnabled(true);
        toEnable.load();
        feature.getMsg(BaseFeature.Message.FEATURE_ENABLED).send(sender, new HashMap<String, String>() {{
            put("%name%", toEnable.getName());
        }});
    }

    private void disableSafe(CommandSender sender, CrispyFeature<?, ?, ?, ?> toDisable) {
        if (!toDisable.isEnabled()) {
            feature.getMsg(BaseFeature.Message.FEATURE_ALREADY_DISABLED).send(sender, new HashMap<String, String>() {{
                put("%name%", toDisable.getName());
            }});
            return;
        }

        toDisable.setEnabled(false);
        toDisable.unload();
        feature.getMsg(BaseFeature.Message.FEATURE_DISABLED).send(sender, new HashMap<String, String>() {{
            put("%name%", toDisable.getName());
        }});
    }

    private void reloadSafe(CommandSender sender, CrispyFeature<?, ?, ?, ?> toReload) {
        if (!toReload.isEnabled()) {
            enableSafe(sender, toReload);
            return;
        }

        boolean restartRequired = !toReload.reload();
        feature.getMsg(BaseFeature.Message.FEATURE_RELOADED).send(sender, new HashMap<String, String>() {{
            put("%name%", toReload.getName());
        }});
        if (restartRequired)
            feature.getMsg(BaseFeature.Message.RESTART_REQUIRED).send(sender);
    }

    @Override
    protected List<String> getTabOptions(Player sender, String[] args) {
        ArrayList<String> options = new ArrayList<>();
        if(args.length == 1)
            options.addAll(Sets.newHashSet("reload", "enable", "disable").stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toSet()));
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("enable")) {
                options.addAll(api.getManager(FeatureManager.class).getFeatures().stream()
                        .filter(f -> !f.isEnabled())
                        .map(CrispyFeature::getName)
                        .filter(s -> s.startsWith(args[1]))
                        .collect(Collectors.toList()));
            } else if (args[0].equalsIgnoreCase("disable")) {
                options.addAll(api.getManager(FeatureManager.class).getFeatures().stream()
                        .filter(CrispyFeature::isEnabled)
                        .map(CrispyFeature::getName)
                        .filter(s -> s.startsWith(args[1]))
                        .collect(Collectors.toList()));
            } else if (args[0].equalsIgnoreCase("reload")) {
                options.addAll(api.getManager(FeatureManager.class).getFeatures().stream()
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

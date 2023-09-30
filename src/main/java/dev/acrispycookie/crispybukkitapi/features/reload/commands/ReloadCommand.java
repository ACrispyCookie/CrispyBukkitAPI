package dev.acrispycookie.crispybukkitapi.features.reload.commands;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.features.FeatureCommand;
import dev.acrispycookie.crispybukkitapi.features.reload.ReloadFeature;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand extends FeatureCommand<ReloadFeature> {

    public ReloadCommand(ReloadFeature feature, CrispyBukkitAPI api) {
        super(feature, api, api.getPlugin().getName(), "Reloads the plugin configuration and data.");
    }

    @Override
    protected boolean runPlayer(Player player, String[] args) {
        if(!player.hasPermission(feature.getPerm(ReloadFeature.PermissionMap.RELOAD.path()))) {
            feature.getMsg(ReloadFeature.MessageMap.NO_PERMISSION.path()).send(player);
            return false;
        }
        if(args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            feature.getMsg(ReloadFeature.MessageMap.USAGE.path()).send(player);
            return false;
        }

        boolean restartRequired = api.reload();
        feature.getMsg(ReloadFeature.MessageMap.RELOADED.path()).send(player);
        if(restartRequired)
            feature.getMsg(ReloadFeature.MessageMap.RESTART_REQUIRED.path()).send(player);
        return true;
    }

    @Override
    protected boolean runConsole(CommandSender sender, String[] args) {
        if(args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            feature.getMsg(ReloadFeature.MessageMap.USAGE.path()).send(sender);
            return false;
        }

        boolean restartRequired = api.reload();
        feature.getMsg(ReloadFeature.MessageMap.RELOADED.path()).send(sender);
        if(restartRequired)
            feature.getMsg(ReloadFeature.MessageMap.RESTART_REQUIRED.path()).send(sender);
        return true;
    }

    @Override
    protected List<String> getTabOptions(Player sender, String[] args) {
        ArrayList<String> options = new ArrayList<>();
        if(args.length == 1) {
            options.add("reload");
        }
        return options;
    }
}

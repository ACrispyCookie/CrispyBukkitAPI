package dev.acrispycookie.crispybukkitapi.features;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispycommons.utility.nms.CommandRegister;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class CrispyFeatureCommand<T extends CrispyFeature<?, ?, ?, ?>> extends BukkitCommand {

    protected final CrispyBukkitAPI api;
    private final String name;
    protected final T feature;
    protected abstract boolean runPlayer(Player player, String[] args);
    protected abstract boolean runConsole(CommandSender sender, String[] args);
    protected abstract List<String> getTabOptions(Player sender, String[] args);

    public CrispyFeatureCommand(T feature, CrispyBukkitAPI api, String name, String description) {
        super(name);
        this.feature = feature;
        this.api = api;
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String usage, String[] args) {
        if (sender instanceof Player) {
            return runPlayer((Player) sender, args);
        }
        else {
            return runConsole(sender, args);
        }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        if (sender instanceof Player) {
            return getTabOptions((Player) sender, args);
        }
        return new ArrayList<>();
    }

    public @NotNull String getName() {
        return name;
    }

    protected void unregister() {
        SimpleCommandMap map = CommandRegister.newInstance().unregister(api.getPlugin(), getLabel());
        this.unregister(map);
    }
}

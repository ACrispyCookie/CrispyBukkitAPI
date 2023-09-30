package dev.acrispycookie.crispybukkitapi.features;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class FeatureCommand<T extends Feature> extends BukkitCommand {

    protected final CrispyBukkitAPI api;
    private final String name;
    private boolean enabled;
    protected final T feature;
    protected abstract boolean runPlayer(Player player, String[] args);
    protected abstract boolean runConsole(CommandSender sender, String[] args);
    protected abstract List<String> getTabOptions(Player sender, String[] args);

    public FeatureCommand(T feature, CrispyBukkitAPI api, String name, String description) {
        super(name);
        this.feature = feature;
        this.api = api;
        this.name = name;
        this.description = description;
        this.enabled = true;
    }

    @Override
    public boolean execute(CommandSender sender, String usage, String[] args) {
        if(!enabled)
            return false;

        if (sender instanceof Player) {
            return runPlayer((Player) sender, args);
        }
        else {
            return runConsole(sender, args);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if(!enabled)
            return new ArrayList<>();

        if (sender instanceof Player) {
            return getTabOptions((Player) sender, args);
        }
        return new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    protected void disable() {
        enabled = false;
    }
}

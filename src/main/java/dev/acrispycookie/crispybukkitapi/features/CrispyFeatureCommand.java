package dev.acrispycookie.crispybukkitapi.features;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public abstract class CrispyFeatureCommand<T extends CrispyFeature> extends BukkitCommand {

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
    public boolean execute(CommandSender sender, String usage, String[] args) {
        if (sender instanceof Player) {
            return runPlayer((Player) sender, args);
        }
        else {
            return runConsole(sender, args);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (sender instanceof Player) {
            return getTabOptions((Player) sender, args);
        }
        return new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    protected void unregister() {
        SimpleCommandMap map = ((CraftServer) api.getPlugin().getServer()).getCommandMap();
        Field knownCommandsField = getField(SimpleCommandMap.class, "knownCommands");
        Map<String, Command> knownCommands = getCommandMap(knownCommandsField, map);
        knownCommands.remove(api.getPlugin().getName().toLowerCase() + ":" + getLabel());
        knownCommands.remove(getLabel());
        this.unregister(map);
        try {
            knownCommandsField.set(map, knownCommands);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Command> getCommandMap(Field field, SimpleCommandMap map) {
        try {
            field.setAccessible(true);
            return (Map<String, Command>) field.get(map);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getField(superClass, fieldName);
            } else {
                return null;
            }
        }
    }
}

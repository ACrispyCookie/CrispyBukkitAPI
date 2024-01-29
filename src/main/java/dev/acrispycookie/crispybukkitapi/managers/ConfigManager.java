package dev.acrispycookie.crispybukkitapi.managers;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.files.SpigotYamlFileManager;
import dev.acrispycookie.crispycommons.api.wrappers.itemstack.CrispyHeadItem;
import dev.acrispycookie.crispycommons.api.wrappers.itemstack.CrispyItemStack;
import dev.acrispycookie.crispycommons.implementations.wrappers.itemstack.PlayerHeadItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ConfigManager extends BaseManager {

    private ConfigInfo defaultConfig;
    private final HashMap<ConfigInfo, SpigotYamlFileManager> configs;
    private final ArrayList<ConfigInfo> toLoad;

    public ConfigManager(CrispyBukkitAPI api) {
        super(api);
        this.configs = new HashMap<>();
        this.toLoad = new ArrayList<>();
        this.defaultConfig = new ConfigInfo("config.yml", "");
        this.toLoad.add(defaultConfig);
    }

    public <T> T getFromType(ConfigInfo config, String path, DataType dataType, Class<T> type) {
        switch (dataType){
            case INTEGER: return type.cast(getInt(config, path));
            case STRING: return type.cast(getString(config, path));
            case DOUBLE: return type.cast(getDouble(config, path));
            case LONG: return type.cast(getLong(config, path));
            case BOOLEAN: return type.cast(getBoolean(config, path));
            case SECTION: return type.cast(getSection(config, path));
            case INTEGER_LIST: return type.cast(getIntList(config, path));
            case DOUBLE_LIST: return type.cast(getDoubleList(config, path));
            case LONG_LIST: return type.cast(getLongList(config, path));
            case BOOLEAN_LIST: return type.cast(getBooleanList(config, path));
            case STRING_LIST: return type.cast(getStringList(config, path));
            case ITEM_BUILDER: return type.cast(getItemBuilder(config, path));
            case SKULL_BUILDER: return type.cast(getSkullBuilder(config, path));
        }
        return null;
    }

    public boolean hasPath(ConfigInfo config, String path) {
        return configs.get(config).get().contains(path);
    }

    public void disableDefault() {
        this.toLoad.remove(defaultConfig);
        defaultConfig = null;
    }

    public void addConfig(ConfigInfo info) {
        toLoad.add(info);
    }

    public void load() throws ManagerLoadException {
        for(ConfigInfo info : toLoad) {
            try {
                configs.put(info, new SpigotYamlFileManager(api.getPlugin(), info.getFile(), info.getDirectory()));
            } catch (IOException | InvalidConfigurationException e) {
                throw new ManagerLoadException(e);
            }
        }
    }

    public void unload() {
        configs.clear();
        toLoad.clear();
        defaultConfig = null;
    }

    public boolean hasDefault() {
        return defaultConfig != null;
    }

    public ConfigInfo getDefault() {
        return defaultConfig;
    }

    private Integer getInt(ConfigInfo config, String path) throws InvalidTypeException {
        if(!configs.get(config).get().isInt(path))
            throw new InvalidTypeException("Value at " + path + " is not an integer.");
        return configs.get(config).get().getInt(path);
    }

    private long getLong(ConfigInfo config, String path) throws InvalidTypeException {
        if(!configs.get(config).get().isLong(path))
            throw new InvalidTypeException("Value at " + path + " is not a long integer.");
        return configs.get(config).get().getLong(path);
    }

    private double getDouble(ConfigInfo config, String path) throws InvalidTypeException {
        if(!configs.get(config).get().isDouble(path))
            throw new InvalidTypeException("Value at " + path + " is not a double.");
        return configs.get(config).get().getDouble(path);
    }

    private String getString(ConfigInfo config, String path) throws InvalidTypeException {
        if(!configs.get(config).get().isString(path))
            throw new InvalidTypeException("Value at " + path + " is not a string.");
        return configs.get(config).get().getString(path);
    }

    private boolean getBoolean(ConfigInfo config, String path) throws InvalidTypeException {
        if(!configs.get(config).get().isBoolean(path))
            throw new InvalidTypeException("Value at " + path + " is not a boolean.");
        return configs.get(config).get().getBoolean(path);
    }

    private List<String> getStringList(ConfigInfo config, String path) throws InvalidTypeException {
        if(!configs.get(config).get().isList(path))
            throw new InvalidTypeException("Value at " + path + " is not a list.");
        return configs.get(config).get().getStringList(path);
    }

    private List<Integer> getIntList(ConfigInfo config, String path) throws InvalidTypeException {
        if(!configs.get(config).get().isList(path))
            throw new InvalidTypeException("Value at " + path + " is not a list.");
        return configs.get(config).get().getIntegerList(path);
    }

    private List<Long> getLongList(ConfigInfo config, String path) throws InvalidTypeException {
        if(!configs.get(config).get().isList(path))
            throw new InvalidTypeException("Value at " + path + " is not a list.");
        return configs.get(config).get().getLongList(path);
    }

    private List<Double> getDoubleList(ConfigInfo config, String path) throws InvalidTypeException {
        if(!configs.get(config).get().isList(path))
            throw new InvalidTypeException("Value at " + path + " is not a list.");
        return configs.get(config).get().getDoubleList(path);
    }

    private List<Boolean> getBooleanList(ConfigInfo config, String path) throws InvalidTypeException {
        if(!configs.get(config).get().isList(path))
            throw new InvalidTypeException("Value at " + path + " is not a list.");
        return configs.get(config).get().getBooleanList(path);
    }

    private ConfigurationSection getSection(ConfigInfo config, String path) throws InvalidTypeException {
        if(!configs.get(config).get().isConfigurationSection(path))
            throw new InvalidTypeException("Value at " + path + " is not a section.");
        return configs.get(config).get().getConfigurationSection(path);
    }

    private CrispyItemStack getItemBuilder(ConfigInfo config, String path){
        if(!configs.get(config).get().isConfigurationSection(path))
            throw new InvalidTypeException("Value at " + path + " is not an item.");

        ConfigurationSection section = getSection(config, path);
        CrispyItemStack itemStackBuilder = new CrispyItemStack(Material.valueOf(section.getString("material")));
        itemStackBuilder.durability((short) section.getInt("data"));
        itemStackBuilder.amount(section.getInt("amount"));
        itemStackBuilder.glint(section.getBoolean("enchanted"));
        itemStackBuilder.name(section.getString("name"));
        itemStackBuilder.hideAttributes(section.getBoolean("hide_attributes"));
        StringBuilder lore = new StringBuilder();
        for(String line : section.getStringList("lore")){
            lore.append(line).append("\n");
        }
        itemStackBuilder.lore(lore.substring(0, Math.max(lore.toString().length() - 1, 0)));
        return itemStackBuilder;
    }

    private CrispyHeadItem getSkullBuilder(ConfigInfo config, String path){
        if(!configs.get(config).get().isConfigurationSection(path))
            throw new InvalidTypeException("Value at " + path + " is not an item.");

        ConfigurationSection section = getSection(config, path);
        CrispyHeadItem itemStackBuilder = new PlayerHeadItem(UUID.fromString(section.getString("owner")));
        itemStackBuilder.amount(section.getInt("amount"));
        itemStackBuilder.name(section.getString("name"));
        StringBuilder lore = new StringBuilder();
        for(String line : section.getStringList("lore")){
            lore.append(line).append("\n");
        }
        itemStackBuilder.lore(lore.substring(0, Math.max(lore.toString().length() - 1, 0)));
        return itemStackBuilder;
    }

    public enum DataType {
        INTEGER,
        LONG,
        DOUBLE,
        BOOLEAN,
        STRING,
        INTEGER_LIST,
        LONG_LIST,
        DOUBLE_LIST,
        BOOLEAN_LIST,
        STRING_LIST,
        SECTION,
        ITEM_BUILDER,
        SKULL_BUILDER
    }

    public static class ConfigInfo {
        private final String file;
        private final String directory;

        public ConfigInfo(String file, String directory) {
            this.file = file;
            this.directory = directory;
        }

        public String getFile() {
            return file;
        }

        public String getDirectory() {
            return directory;
        }
    }

    public static class InvalidTypeException extends RuntimeException {
        public InvalidTypeException(String errorMessage) {
            super(errorMessage);
        }
    }

    @Override
    public void reload() throws ManagerReloadException {
        for (ConfigInfo i : configs.keySet()) {
            try {
                configs.get(i).reload();
            } catch (IOException | InvalidConfigurationException e) {
                throw new ManagerReloadException(e, true, true);
            }
        }
    }
}

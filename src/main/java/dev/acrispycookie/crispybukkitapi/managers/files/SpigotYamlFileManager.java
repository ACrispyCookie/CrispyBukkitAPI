package dev.acrispycookie.crispybukkitapi.managers.files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class SpigotYamlFileManager extends DataFileManager {

    YamlConfiguration yaml;

    public SpigotYamlFileManager(JavaPlugin plugin, String name, String directory) throws IOException, InvalidConfigurationException {
        super(plugin, name, directory);
        yaml = new YamlConfiguration();
        yaml.load(getFile());
    }

    public YamlConfiguration get() {
        return yaml;
    }

    public void reload() throws IOException, InvalidConfigurationException {
        super.reload();
        yaml = new YamlConfiguration();
        yaml.load(getFile());
    }
}

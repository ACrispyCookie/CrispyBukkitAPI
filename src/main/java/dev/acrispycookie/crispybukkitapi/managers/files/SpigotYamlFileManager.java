package dev.acrispycookie.crispybukkitapi.managers.files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class SpigotYamlFileManager extends DataFileManager {

    YamlConfiguration yaml;

    public SpigotYamlFileManager(JavaPlugin plugin, String name, String directory) {
        super(plugin, name, directory);

        try {
            yaml = new YamlConfiguration();
            yaml.load(getFile());
        } catch (InvalidConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public YamlConfiguration get() {
        return yaml;
    }

    public void reload() {
        super.reload();
        try {
            yaml = new YamlConfiguration();
            yaml.load(getFile());
        } catch (InvalidConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}

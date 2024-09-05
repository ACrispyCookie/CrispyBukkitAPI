package dev.acrispycookie.crispybukkitapi.files;

import dev.acrispycookie.crispycommons.utility.logging.CrispyLogger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;

public class SpigotYamlFileManager extends DataFileManager {

    private YamlConfiguration yaml;
    private int missingFields;

    public SpigotYamlFileManager(JavaPlugin plugin, String name, String directory) throws IOException, InvalidConfigurationException {
        super(plugin, name, directory);
        yaml = new YamlConfiguration();
        yaml.load(getFile());
        missingFields = loadMissingFields();
        if (missingFields != 0)
            CrispyLogger.log(plugin, Level.WARNING, "Configuration \"" + name + "\" was missing " + missingFields + " field" + (missingFields != 1 ? "s!" : "!"));
        CrispyLogger.log(plugin, Level.INFO, "Loaded configuration \"" + name + "\"!");
    }

    public YamlConfiguration get() {
        return yaml;
    }

    public void set(String path, Object value) {
        yaml.set(path, value);
        try {
            yaml.save(getFile());
        } catch (IOException e) {
            CrispyLogger.printException(plugin, e, "Failed to save the config: " + getName());
        }
    }

    public void reload() throws IOException, InvalidConfigurationException {
        super.reload();
        yaml = new YamlConfiguration();
        yaml.load(getFile());
        missingFields = loadMissingFields();
    }

    public int getMissingFields() {
        return missingFields;
    }

    private int loadMissingFields() {
        try {
            YamlConfiguration original = new YamlConfiguration();
            original.loadFromString(getOriginalContent());
            int count = 0;
            for (String field : original.getKeys(true)) {
                if (yaml.contains(field))
                    continue;
                set(field, original.get(field));
                ++count;
            }
            return count;
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}

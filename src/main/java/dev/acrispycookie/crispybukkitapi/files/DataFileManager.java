package dev.acrispycookie.crispybukkitapi.files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public abstract class DataFileManager {

    private final JavaPlugin plugin;
    private final String name;
    private final String directory;
    private File file;
    public abstract Object get();

    public DataFileManager(JavaPlugin plugin, String name, String directory) {
        this.plugin = plugin;
        this.name = name;
        this.directory = directory;
        loadFile();
    }

    private void loadFile() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        File file = new File(plugin.getDataFolder() + "/", name);
        if (!file.exists()) {
            plugin.saveResource(directory + name, false);
            file = new File(plugin.getDataFolder() + "/" + directory, name);
            file.renameTo(new File(plugin.getDataFolder() + "/", name));
            file = new File(plugin.getDataFolder() + "/", name);
            File folder = new File(plugin.getDataFolder() + "/" + directory);
            folder.delete();
        }
        this.file = file;
    }

    public void reload() throws IOException, InvalidConfigurationException {
        loadFile();
    }

    public String getName() {
        return name;
    }

    public String getDirectory() {
        return directory;
    }

    public File getFile() {
        return file;
    }
}

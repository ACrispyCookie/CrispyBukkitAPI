package dev.acrispycookie.crispybukkitapi.files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public abstract class DataFileManager {

    protected final JavaPlugin plugin;
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
            try {
                InputStream stream = plugin.getResource(directory + name);
                if (stream == null)
                    return;
                file.createNewFile();
                writeToFile(stream, file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

    protected String getOriginalContent() {
        try {
            InputStream inputStream = plugin.getResource(directory + name);
            if (inputStream == null)
                return null;
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append(System.lineSeparator());
                }
            }
            return stringBuilder.toString();
        } catch (IOException exception) {
            System.out.println("Error converting an input stream to string");
            return null;
        }
    }

    protected String getDefaultContent() {
        try {
            InputStream inputStream = plugin.getResource(directory + "default-" + name);
            if (inputStream == null)
                return null;
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append(System.lineSeparator());
                }
            }
            return stringBuilder.toString();
        } catch (IOException exception) {
            System.out.println("Error converting an input stream to string");
            return null;
        }
    }

    private void writeToFile(InputStream inputStream, File file) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}

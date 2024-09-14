package dev.acrispycookie.crispypluginapi.files;


import dev.acrispycookie.crispypluginapi.CrispyPluginAPI;

import java.io.*;

public abstract class DataFileManager {

    protected final CrispyPluginAPI api;
    private final String name;
    private final String directory;
    private File file;
    public abstract Object get();

    public DataFileManager(CrispyPluginAPI api, String name, String directory) {
        this.api = api;
        this.name = name;
        this.directory = directory;
        loadFile();
    }

    private void loadFile() {
        if (!api.getPlugin().getDataFolder().exists()) {
            api.getPlugin().getDataFolder().mkdir();
        }

        File file = new File(api.getPlugin().getDataFolder() + "/", name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.file = file;
    }

    public void reload() throws IOException {
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

    protected InputStream getOriginalContent() {
        return api.getPlugin().getResource(directory + name);
    }

    protected InputStream getDefaultContent() {
        return api.getPlugin().getResource(directory + "default-" + name);
    }
}

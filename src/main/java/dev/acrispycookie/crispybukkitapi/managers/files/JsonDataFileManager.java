package dev.acrispycookie.crispybukkitapi.managers.files;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class JsonDataFileManager extends DataFileManager {

    JsonObject json;

    public JsonDataFileManager(JavaPlugin plugin, String name, String directory) {
        super(plugin, name, directory);

        try {
            json = new Gson().fromJson(new FileReader(getFile()), JsonObject.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonObject get() {
        return json;
    }
}

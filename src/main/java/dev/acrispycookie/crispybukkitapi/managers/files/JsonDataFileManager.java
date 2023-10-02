package dev.acrispycookie.crispybukkitapi.managers.files;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class JsonDataFileManager extends DataFileManager {

    JsonObject json;

    public JsonDataFileManager(JavaPlugin plugin, String name, String directory) throws FileNotFoundException {
        super(plugin, name, directory);
        json = new Gson().fromJson(new FileReader(getFile()), JsonObject.class);
    }

    public JsonObject get() {
        return json;
    }
}

package dev.acrispycookie.crispypluginapi.files;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.acrispycookie.crispypluginapi.CrispyPluginAPI;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class JsonFileManager extends DataFileManager {

    private final JsonObject json;

    public JsonFileManager(CrispyPluginAPI api, String name, String directory) throws FileNotFoundException {
        super(api, name, directory);
        json = new Gson().fromJson(new FileReader(getFile()), JsonObject.class);
    }

    public JsonObject get() {
        return json;
    }
}

package dev.acrispycookie.crispybukkitapi.managers;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.managers.files.SpigotYamlFileManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.Map;

public class LanguageManager extends BaseManager {

    private static final boolean HEX_SUPPORT = false;
    private boolean enabled = true;
    private SpigotYamlFileManager yamlManager;

    public LanguageManager(CrispyBukkitAPI api) {
        super(api);
    }

    public void disableDefault() {
        enabled = false;
    }

    public void load() throws ManagerLoadException {
        if(enabled) {
            try {
                this.yamlManager = new SpigotYamlFileManager(api.getPlugin(), "lang.yml", "");
            } catch (IOException | InvalidConfigurationException e) {
                throw new ManagerLoadException(e);
            }
        }
    }

    public TextComponent get(String path) {
        if(!yamlManager.get().isConfigurationSection(path))
            return new TextComponent(translate(buildListString(path)));
        return buildComponent(path);
    }

    public TextComponent get(String path, Map<String, String> placeholders) {
        if(!yamlManager.get().isConfigurationSection(path))
            return new TextComponent(parsePlaceholders(translate(buildListString(path)), placeholders));
        return buildComponent(path);
    }

    private TextComponent buildComponent(String path) {
        path = addRootPath(path);
        TextComponent component = new TextComponent(translate(buildListString(path + ".text")));
        if (yamlManager.get().contains(path + ".hover-text")) {
            component.setHoverEvent(
                    new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new TextComponent[] { new TextComponent(translate(buildListString(path + ".hover-text"))) }
                    )
            );
        }
        if(yamlManager.get().contains(path + ".click-action")) {
            component.setClickEvent(
                    new ClickEvent(
                            ClickEvent.Action.valueOf(yamlManager.get().getString(path + ".click-action")),
                            yamlManager.get().getString(path + ".click-data")
                    )
            );
        }
        return component;
    }

    private String buildListString(String path) {
        path = addRootPath(path);
        if(!yamlManager.get().getStringList(path).isEmpty()) {
            StringBuilder s = new StringBuilder();
            for(String ss : yamlManager.get().getStringList(path)){
                s.append(ss).append("\n");
            }
            s = new StringBuilder(s.substring(0, Math.max(s.length() - 1, s.length())));
            return s.toString();
        } else {
            return yamlManager.get().getString(path);
        }
    }

    private String parsePlaceholders(String msg, Map<String, String> placeholders) {
        for (String placeholder : placeholders.keySet()) {
            msg = msg.replace(placeholder, placeholders.get(placeholder));
        }
        return msg;
    }

    private String translate(String msg) {
        if(!HEX_SUPPORT) {
            return ChatColor.translateAlternateColorCodes('&', msg);
        }
        return msg;
    }

    private String addRootPath(String path) {
        return "features." + path;
    }

    @Override
    public void reload() throws ManagerReloadException {
        try {
            yamlManager.reload();
        } catch (IOException | InvalidConfigurationException e) {
            throw new ManagerReloadException(e, true, true);
        }
    }
}

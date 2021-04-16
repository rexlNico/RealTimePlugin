package de.rexlnico.realtimeplugin.methodes;

import de.rexlnico.realtimeplugin.main.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Messages {

    public static final YamlConfiguration cfg = new YamlConfiguration();

    public static String prefix = "";
    public static String reloadFileMSG = "";
    public static String reloadAllMSG = "";
    public static String reloadHelp = "";
    public static String noFile = "";
    public static String noPermissions = "";

    public static void load() {
        File file = new File(Main.getPlugin().getDataFolder(), "messages.cfg");
        if (file.exists()) {
            try {
                cfg.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                Main.getPlugin().getLogger().log(Level.SEVERE, "Could not load messages.cfg!", e);
                return;
            }
        }

        cfg.addDefault("Prefix", "&8[&eRealtime&8] ");
        cfg.addDefault("reloadFileMSG", "successfully reloaded %file%");
        cfg.addDefault("reloadAllMSG", "successfully reloaded!");
        cfg.addDefault("reloadHelp", "&cPlease use /realtime reload (file)");
        cfg.addDefault("noFile", "&cThe file %file% does not exist!");
        cfg.addDefault("noPermsMSG", "You have insufficient permissions to perform this action!");
        cfg.options().copyDefaults(true);
        try {
            cfg.save(file);
        } catch (Exception e) {
            Main.getPlugin().getLogger().log(Level.WARNING, "Could not save messages.cfg!", e);
        }
        prefix = cfg.getString("Prefix").replace("&", "§");
        reloadFileMSG = cfg.getString("reloadFileMSG").replace("&", "§");
        reloadAllMSG = cfg.getString("reloadAllMSG").replace("&", "§");
        reloadHelp = cfg.getString("reloadHelp").replace("&", "§");
        noFile = cfg.getString("noFile").replace("&", "§");
        noPermissions = cfg.getString("noPermsMSG").replace("&", "§");
    }

}

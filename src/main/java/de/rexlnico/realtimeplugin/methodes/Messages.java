package de.rexlnico.realtimeplugin.methodes;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Messages {

    public static final File file = new File("plugins/RealTimePlugin/messages.cfg");
    public static final YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    public static String prefix = "";
    public static String reloadFileMSG = "";
    public static String reloadAllMSG = "";
    public static String reloadHelp = "";
    public static String noFile = "";
    public static String noPermissions = "";

    public static void load() {
        cfg.addDefault("Prefix", "&8[&eRealtime&8] ");
        cfg.addDefault("reloadFileMSG", "successfully reloaded %file%");
        cfg.addDefault("reloadAllMSG", "successfully reloaded!");
        cfg.addDefault("reloadHelp", "&cPlease use /realtime reload <file>");
        cfg.addDefault("noFile", "&cThe file %file% does not exist!");
        cfg.addDefault("noPermsMSG", "You have no permissions!");
        cfg.options().copyDefaults(true);
        try {
            cfg.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        prefix = cfg.getString("Prefix").replace("&", "§");
        reloadFileMSG = cfg.getString("reloadFileMSG").replace("&", "§");
        reloadAllMSG = cfg.getString("reloadAllMSG").replace("&", "§");
        reloadHelp = cfg.getString("reloadHelp").replace("&", "§");
        noFile = cfg.getString("noFile").replace("&", "§");
        noPermissions = cfg.getString("noPermsMSG").replace("&", "§");
    }

}

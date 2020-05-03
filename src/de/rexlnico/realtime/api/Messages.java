package de.rexlnico.realtime.api;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

public class Messages {

    public static final File file = new File("plugins/RealTime/messages.cfg");
    public static final YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    public static String prefix = "";
    public static String reloadMSG = "";
    public static String reloadHelp = "";
    public static String noPermissions = "";

    public static void load() {
        cfg.addDefault("Prefix", "&8[&eRealtime&8] ");
        cfg.addDefault("reloadMSG", "The Config are reloadet!");
        cfg.addDefault("reloadHelp", "&cPlease use /realtime reload!");
        cfg.addDefault("noPermsMSG", "You have no permissions!");
        cfg.options().copyDefaults(true);
        try {
            cfg.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        prefix = cfg.getString("Prefix").replace("&", "§");
        reloadMSG = cfg.getString("reloadMSG").replace("&", "§");
        reloadHelp = cfg.getString("reloadHelp").replace("&", "§");
        noPermissions = cfg.getString("noPermsMSG").replace("&", "§");
    }

}

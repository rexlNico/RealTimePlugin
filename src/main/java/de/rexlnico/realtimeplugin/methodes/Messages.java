package de.rexlnico.realtimeplugin.methodes;

import de.rexlnico.realtimeplugin.main.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

public class Messages {

    public static final YamlConfiguration cfg = new YamlConfiguration();

    public static String prefix = "";
    public static String reloadFileMSG = "";
    public static String reloadAllMSG = "";
    public static String timeMSG = "";
    public static String reloadHelp = "";
    public static String timeHelp = "";
    public static String noFile = "";
    public static String noPermissions = "";

    public static String currentVersion = "";
    public static String newestVersion = "";

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
        cfg.addDefault("timeMSG", "current time for %file% is %time%");
        cfg.addDefault("reloadHelp", "&cPlease use /realtime reload (file)");
        cfg.addDefault("timeHelp", "&cPlease use /realtime time (file)");
        cfg.addDefault("noFile", "&cThe file %file% does not exist!");
        cfg.addDefault("noPermsMSG", "You have insufficient permissions to perform this action!");
        cfg.addDefault("currentVersion", "Current version: %version%");
        cfg.addDefault("newestVersion", "Newest version: %version%");
        cfg.options().copyDefaults(true);
        try {
            cfg.save(file);
        } catch (Exception e) {
            Main.getPlugin().getLogger().log(Level.WARNING, "Could not save messages.cfg!", e);
        }
        prefix = Objects.requireNonNull(cfg.getString("Prefix")).replace("&", "§");
        reloadFileMSG = Objects.requireNonNull(cfg.getString("reloadFileMSG")).replace("&", "§");
        reloadAllMSG = Objects.requireNonNull(cfg.getString("reloadAllMSG")).replace("&", "§");
        timeMSG = Objects.requireNonNull(cfg.getString("timeMSG")).replace("&", "§");
        reloadHelp = Objects.requireNonNull(cfg.getString("reloadHelp")).replace("&", "§");
        timeHelp = Objects.requireNonNull(cfg.getString("timeHelp")).replace("&", "§");
        noFile = Objects.requireNonNull(cfg.getString("noFile")).replace("&", "§");
        noPermissions = Objects.requireNonNull(cfg.getString("noPermsMSG")).replace("&", "§");
        currentVersion = Objects.requireNonNull(cfg.getString("currentVersion")).replace("&", "§");
        newestVersion = Objects.requireNonNull(cfg.getString("newestVersion")).replace("&", "§");
    }

}

package de.rexlnico.realtime.api;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import de.rexlnico.realtime.main.Main;

public class TimeUpdater {

    private File file;
    private YamlConfiguration cfg;
    private Main instance;
    private ArrayList<String> worlds;
    private long intervall;
    private boolean active;
    private String timezone;
    private boolean timer = false;
    private boolean useKey;
    private String key;
    private boolean updateMessage;
    private int taskID;

    public TimeUpdater(Main instance) {
        this.file = new File("plugins/RealTime/config.cfg");
        this.cfg = YamlConfiguration.loadConfiguration(file);
        this.instance = instance;
        ArrayList<String> bspworlds = new ArrayList<>();
        bspworlds.add("world");
        cfg.addDefault("Worlds", bspworlds);
        cfg.addDefault("UpdateIntervall", (long) 10);
        cfg.addDefault("Time.Active", true);
        cfg.addDefault("Time.Timezone", "Europe/Berlin");
        cfg.addDefault("Weather.Active", true);
        cfg.addDefault("Weather.Country", "Germany");
        cfg.addDefault("Weather.City", "Berlin");
        cfg.addDefault("Weather.UseKey", false);
        cfg.addDefault("Weather.Key", "");
        cfg.addDefault("Weather.getKey", "https://home.openweathermap.org/users/sign_up");
        cfg.addDefault("UpdateMessage", true);
        cfg.options().copyDefaults(true);
        try {
            cfg.save(this.file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.worlds = (ArrayList<String>) cfg.getStringList("Worlds");
        this.intervall = cfg.getLong("UpdateInervall");
        this.active = cfg.getBoolean("Time.Active");
        this.timezone = cfg.getString("Time.Timezone");
        this.key = cfg.getString("Weather.Key");
        this.useKey = cfg.getBoolean("Weather.UseKey");
        this.updateMessage = cfg.getBoolean("UpdateMessage");
        if (this.intervall <= 10) {
            this.intervall = 10;
        }
        for (String world : this.worlds) {
            World w = Bukkit.getWorld(world);
            if (w != null) {
                w.setGameRuleValue("doDaylightCycle", "true");
            }
        }
    }

    public boolean isUpdateMessage() {
        return updateMessage;
    }

    public void startTimer() {
        if (!timer) {
            for (String world : this.worlds) {
                World w = Bukkit.getWorld(world);
                if (w != null) {
                    w.setGameRuleValue("doDaylightCycle", "false");
                }
            }
            timer = true;
            ZoneId zone = ZoneId.of(timezone);
            this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.instance, () -> {
                for (String world : TimeUpdater.this.worlds) {
                    if (Bukkit.getWorld(world) != null) {
                        World w = Bukkit.getWorld(world);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HHmm");
                        Instant nowUtc = Instant.now();
                        ZonedDateTime dateTime = ZonedDateTime.ofInstant(nowUtc, zone);
                        String rdat = dtf.format(dateTime) + "0";
                        int time = Integer.parseInt(rdat);
                        time -= 6000;
                        if (time < 0) {
                            time += 24000;
                        }
                        w.setTime(time);
                    }
                }
            }, 0, 20 * intervall);
        }
    }

    public void stopTimer() {
        if (timer) {
            Bukkit.getScheduler().cancelTask(taskID);
            timer = false;
        }
    }

    public boolean isUseKey() {
        return useKey;
    }

    public String getKey() {
        return key;
    }

    public boolean isActive() {
        return active;
    }
}

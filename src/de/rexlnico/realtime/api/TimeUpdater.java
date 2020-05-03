package de.rexlnico.realtime.api;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private int taskID;

    public TimeUpdater(Main instance) {
        this.file = new File("plugins/RealTime/config.cfg");
        this.cfg = YamlConfiguration.loadConfiguration(file);
        this.instance = instance;
        ArrayList<String> bspworlds = new ArrayList<>();
        bspworlds.add("world");
        cfg.addDefault("Worlds", bspworlds);
        cfg.addDefault("UpdateInervall", (long) 10);
        cfg.addDefault("Time.Active", true);
        cfg.addDefault("Time.Timezone", "Europe/Berlin");
        cfg.addDefault("Weather.Active", true);
        cfg.addDefault("Weather.Country", "Germany");
        cfg.addDefault("Weather.City", "Berlin");
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
        if (this.intervall <= 0) {
            this.intervall = 10;
        }
        for (String world : this.worlds) {
            World w = Bukkit.getWorld(world);
            if (w != null) {
                w.setGameRuleValue("doDaylightCycle", "true");
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void startTimer() {
        if (!timer) {
            for (String world : this.worlds) {
                World w = Bukkit.getWorld(world);
                if (w != null) {
                    w.setGameRuleValue("doDaylightCycle", "false");
                }
            }
            timer = true;
            this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.instance, new Runnable() {

                @Override
                public void run() {
                    for (String world : TimeUpdater.this.worlds) {
                        if (Bukkit.getWorld(world) != null) {
                            World w = Bukkit.getWorld(world);
                            DateFormat dg = new SimpleDateFormat("HHmm");
                            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone), Locale.getDefault());
                            Date date = calendar.getTime();
                            String rdat = String.valueOf(dg.format(date)) + "0";
                            int time = Integer.parseInt(rdat);
                            time -= 6000;
                            if (time < 0) {
                                time += 24000;
                            }
                            w.setTime(time);
                        }
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

    public boolean isActive() {
        return active;
    }
}

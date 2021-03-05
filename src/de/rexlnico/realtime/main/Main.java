package de.rexlnico.realtime.main;

import de.rexlnico.realtime.api.Update;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.rexlnico.realtime.api.Messages;
import de.rexlnico.realtime.api.TimeUpdater;
import de.rexlnico.realtime.api.WeatherUpdater;
import de.rexlnico.realtime.commands.Reload;
import de.rexlnico.realtime.listeners.Join;

import java.io.*;
import java.time.ZoneId;
import java.util.ArrayList;

public class Main extends JavaPlugin {

    private static Main instance;
    private TimeUpdater timeUpdate;
    private WeatherUpdater weatherUpdater;
    private PluginManager pluginManager;
    private Update update;

    @Override
    public void onEnable() {
        instance = this;
        pluginManager = Bukkit.getPluginManager();
        timeUpdate = new TimeUpdater(getInstance());
        weatherUpdater = new WeatherUpdater(getInstance());
        update = new Update(getInstance());

        pluginManager.registerEvents(update, this);
        pluginManager.registerEvents(new Join(), this);
        getCommand("realtime").setExecutor(new Reload());

        if (timeUpdate.isActive()) timeUpdate.startTimer();
        if (weatherUpdater.isActive()) weatherUpdater.startTimer();
        Bukkit.getConsoleSender().sendMessage("[RealTime] wurde gestartet!");
        Messages.load();

        ArrayList<String> list = new ArrayList<>();
        for (String s : ZoneId.getAvailableZoneIds()) {
            list.add(s);
        }
        File file = new File("plugins/RealTime/examples/timezones.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        cfg.set("Timezones", list);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        timeUpdate.stopTimer();
        Bukkit.getConsoleSender().sendMessage("[RealTime] wurde gestopt!");
    }

    public TimeUpdater getTimeUpdater() {
        return timeUpdate;
    }

    public WeatherUpdater getWeatherUpdater() {
        return weatherUpdater;
    }

    public static Main getInstance() {
        return instance;
    }

    public void update() {
        timeUpdate.stopTimer();
        weatherUpdater.stopTimer();
        timeUpdate = new TimeUpdater(getInstance());
        weatherUpdater = new WeatherUpdater(getInstance());

        if (timeUpdate.isActive()) timeUpdate.startTimer();
        if (weatherUpdater.isActive()) weatherUpdater.startTimer();
    }

}

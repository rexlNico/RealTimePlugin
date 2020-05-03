package de.rexlnico.realtime.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import de.rexlnico.realtime.main.Main;

public class WeatherChange implements Listener {

    @EventHandler
    public void on(WeatherChangeEvent e) {
        if (Main.getInstance().getWeatherUpdater().isTimerActive()) {
            if (Main.getInstance().getWeatherUpdater().getWorlds().contains(e.getWorld().getName())) {
                e.setCancelled(true);
            }
        }
    }

}

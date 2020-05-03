package de.rexlnico.realtime.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.rexlnico.realtime.main.Main;

public class Join implements Listener {

    @EventHandler
    public void on(PlayerJoinEvent e) {
        Main.getInstance().getWeatherUpdater().setWeather(e.getPlayer());
    }

}

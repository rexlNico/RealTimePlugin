package de.rexlnico.realtimeplugin.main;

import de.rexlnico.realtimeplugin.commands.Commands;
import de.rexlnico.realtimeplugin.methodes.Messages;
import de.rexlnico.realtimeplugin.methodes.WorldManager;
import de.rexlnico.realtimeplugin.util.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Main extends JavaPlugin {

    private static Main plugin;
    private static Metrics metrics;
    private static WorldManager worldManager;

    public static WorldManager getWorldManager() {
        return worldManager;
    }

    public static Main getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        Messages.load();
        getCommand("realtime").setTabCompleter(new Commands());
        getCommand("realtime").setExecutor(new Commands());
        metrics = new Metrics(this, 11510);
        try {
            worldManager = new WorldManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Metrics getMetrics() {
        return metrics;
    }

    @Override
    public void onDisable() {
        worldManager.disable();
    }
}

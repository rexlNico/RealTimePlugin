package de.rexlnico.realtimeplugin.main;

import de.rexlnico.realtimeplugin.commands.Commands;
import de.rexlnico.realtimeplugin.methodes.Messages;
import de.rexlnico.realtimeplugin.methodes.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Main extends JavaPlugin {

    private static Main plugin;
    private PluginManager pm;

    private static WorldManager worldManager;

    @Override
    public void onEnable() {
        plugin = this;
        pm = Bukkit.getPluginManager();
        Messages.load();
        getCommand("realtime").setTabCompleter(new Commands());
        getCommand("realtime").setExecutor(new Commands());
        try {
            worldManager = new WorldManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        worldManager.disable();
    }

    public static WorldManager getWorldManager() {
        return worldManager;
    }

    public static Main getPlugin() {
        return plugin;
    }
}

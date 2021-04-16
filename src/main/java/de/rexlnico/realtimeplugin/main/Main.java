package de.rexlnico.realtimeplugin.main;

import de.rexlnico.realtimeplugin.commands.Commands;
import de.rexlnico.realtimeplugin.methodes.Messages;
import de.rexlnico.realtimeplugin.methodes.WorldManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Main extends JavaPlugin {

    private static Main plugin;
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
}

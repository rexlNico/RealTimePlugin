package de.rexlnico.realtimeplugin.main;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.rexlnico.realtimeplugin.commands.Commands;
import de.rexlnico.realtimeplugin.methodes.Messages;
import de.rexlnico.realtimeplugin.methodes.WorldManager;
import de.rexlnico.realtimeplugin.util.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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
        Objects.requireNonNull(getCommand("realtime")).setTabCompleter(new Commands());
        Objects.requireNonNull(getCommand("realtime")).setExecutor(new Commands());
        metrics = new Metrics(this, 11510);
        try {
            worldManager = new WorldManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentVersion() {
        return getDescription().getVersion();
    }

    public String getNewestVersion() {
        try {
            String body = new java.util.Scanner(new java.net.URL("https://api.spiget.org/v2/resources/69545/versions/latest")
                    .openStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
            //JsonObject asJsonObject = new JsonParser().parse(body).getAsJsonObject();
            JsonObject asJsonObject = JsonParser.parseString(body).getAsJsonObject();
            return asJsonObject.get("name").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getCurrentVersion();
    }

    public static Metrics getMetrics() {
        return metrics;
    }

    @Override
    public void onDisable() {
        worldManager.disable();
    }
}

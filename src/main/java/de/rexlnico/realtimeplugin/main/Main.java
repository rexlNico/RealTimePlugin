package de.rexlnico.realtimeplugin.main;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.rexlnico.realtimeplugin.commands.Commands;
import de.rexlnico.realtimeplugin.methodes.Messages;
import de.rexlnico.realtimeplugin.methodes.WorldManager;
import de.rexlnico.realtimeplugin.util.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import sun.security.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.nio.charset.Charset;

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

    public String getCurrentVersion() {
        return getDescription().getVersion();
    }

    public String getNewestVersion() {
        try {
            String body = new java.util.Scanner(new java.net.URL("https://api.spiget.org/v2/resources/69545/versions/latest")
                    .openStream(), "UTF-8").useDelimiter("\\A").next();
            JsonObject asJsonObject = new JsonParser().parse(body).getAsJsonObject();
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

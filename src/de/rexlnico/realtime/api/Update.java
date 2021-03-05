package de.rexlnico.realtime.api;

import de.rexlnico.realtime.main.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Update implements Listener {

    private Main instance;
    private String version;
    private String newest;
    private boolean upToDate;

    public Update(Main instance) {
        this.instance = instance;
        PluginDescriptionFile pdf = instance.getDescription();
        version = pdf.getVersion();
        newest = getNewestVersion();
        upToDate = (version.equals(newest));
        update();
    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (instance.getTimeUpdater().isUpdateMessage()) {
            if (player.hasPermission("realtime.update")) {
                if (!upToDate) {
                    player.sendMessage("§4RealTimePlugin is not up to date! §eYour version: " + version + ", newest version: " + newest);
                    TextComponent textComponent = new TextComponent("§eYou can download the newest version §4Here!");
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, getDownloadForVersion(newest)));
                    player.spigot().sendMessage(textComponent);
                }
            }
        }
    }

    public String getNewestVersion() {
        try {
            String version = "";
            String url = "http://version.rexlnico.de";
            String source = getUrlSource(url);
            JSONObject object = toJSON(source);
            version = (String) ((JSONObject) ((JSONArray) object.get("realtime")).get(0)).get("newestVersion");
            return version.toLowerCase();
        } catch (Exception e) {
            return "";
        }
    }

    public String getDownloadForVersion(String version) {
        try {
            String link = "";
            String url = "http://version.rexlnico.de";
            String source = getUrlSource(url);
            JSONObject object = toJSON(source);
            link = (String) ((JSONObject) ((JSONArray) object.get("realtime")).get(0)).get(version);
            return link.toLowerCase();
        } catch (Exception e) {
            return "https://www.spigotmc.org/resources/real-time-plugin.69545/history";
        }
    }

    private String getUrlSource(String url) {
        try {

            URL url2 = null;
            try {
                url2 = new URL(url);
            } catch (MalformedURLException ex) {
            }
            URLConnection yc = null;
            try {
                yc = url2.openConnection();
            } catch (IOException ex2) {
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(yc.getInputStream(), "UTF-8"));
            } catch (IOException ex3) {
            }
            StringBuilder a = new StringBuilder();
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    a.append(inputLine);
                }
            } catch (IOException ex4) {
            }
            try {
                in.close();
            } catch (IOException ex5) {
            }
            return a.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private void update() {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(instance, () -> {
            newest = getNewestVersion();
            upToDate = (version.equals(newest));
        }, 0, 20 * 60 * 5);
    }

    private JSONObject toJSON(String json) {
        return (JSONObject) JSONValue.parse(json);
    }

}

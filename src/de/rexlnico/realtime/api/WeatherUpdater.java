package de.rexlnico.realtime.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import de.rexlnico.realtime.main.Main;

public class WeatherUpdater {

    private File file;
    private YamlConfiguration cfg;
    private Main instance;
    private ArrayList<String> worlds;
    private long intervall;
    private String country;
    private String city;
    private boolean active;
    private boolean timer = false;
    private int taskID;

    private ArrayList<String> keys;

    public WeatherUpdater(Main instance) {
        keys = new ArrayList<>();
        keys.add("c91a880d4154c9782019a010b1e312a7");
        keys.add("16e8ffada1fbdbe3f3903802b0785751");
        keys.add("1f8280a6c1853939d4056dae30554007");
        keys.add("c47e79a7cf6aeab9007638a3b8d27392");
        keys.add("f294c04f34a16480a264b6d339b7d118");
        this.file = new File("plugins/RealTime/config.cfg");
        this.cfg = YamlConfiguration.loadConfiguration(file);
        this.instance = instance;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ArrayList<String> bspworlds = new ArrayList<>();
        bspworlds.add("world");
        cfg.addDefault("Worlds", bspworlds);
        cfg.addDefault("UpdateIntervall", (long) 10);
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
        this.intervall = cfg.getLong("UpdateIntervall");
        this.country = cfg.getString("Weather.Country");
        this.city = cfg.getString("Weather.City");
        this.active = cfg.getBoolean("Weather.Active");
        if (this.intervall <= 30) {
            this.intervall = 30;
        }
    }

    public void startTimer() {
        if (!timer) {
            for (String world : this.worlds) {
                World w = Bukkit.getWorld(world);
                if (w != null) {
                    w.setStorm(false);
                }
            }
            timer = true;
            this.taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(this.instance, new Runnable() {

                @Override
                public void run() {
                    for (String world : WeatherUpdater.this.worlds) {
                        if (Bukkit.getWorld(world) != null) {
                            World w = Bukkit.getWorld(world);
                            for (Player a : w.getPlayers()) {
                                setWeather(a);
                            }
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

    private int count = 0;

    public void setWeather(Player player) {
        String weather = getWeather(0, 1);
        switch (weather) {
            case "rain":
                try {
                    sendState(player, 2, 0.0f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "thunderstorm":
                try {
                    sendState(player, 2, 0.0f);
                    if (count == 1) {
                        count = 0;
                    } else {
                        sendLightning(player, getRandomLocation(player));
                        count++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                try {
                    sendState(player, 1, 0.0f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private String getUrlSource(String url) {
        try {
            URL url2 = null;
            url2 = new URL(url);
            URLConnection yc = null;
            yc = url2.openConnection();
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(yc.getInputStream(), "UTF-8"));
            final StringBuilder a = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                a.append(inputLine);
            }
            in.close();
            return a.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private JSONObject toJSON(final String json) {
        return (JSONObject) JSONValue.parse(json);
    }

    public void sendState(Player player, int type, float state) throws InstantiationException, NoSuchFieldException {
        try {
            final Object entityPlayer = player.getClass().getMethod("getHandle", (Class<?>[]) new Class[0]).invoke(player, new Object[0]);
            final Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
            final Object packet = getNMSClass("PacketPlayOutGameStateChange").getConstructor(Integer.TYPE, Float.TYPE).newInstance(type, state);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception ignored) {
        }
    }

    public Location getRandomLocation(Player player) {
        Location location;
        int x;
        int z;
        x = new Random().nextInt(player.getLocation().getBlockX() + 25) + 12;
        z = new Random().nextInt(player.getLocation().getBlockZ() + 25) + 12;
        if (new Random().nextBoolean()) {
            x *= -1;
        }
        if (new Random().nextBoolean()) {
            z *= -1;
        }
        location = new Location(player.getWorld(), player.getLocation().add(x, 0, z).getX(), player.getWorld().getHighestBlockYAt(x, z) + 1, player.getLocation().add(x, 0, z).getZ());
        return location;
    }

    private void sendLightning(Player player, Location l) {
        Class<?> light = getNMSClass("EntityLightning");
        try {
            Constructor<?> constu = light.getConstructor(getNMSClass("World"), double.class, double.class, double.class, boolean.class, boolean.class);
            Object wh = player.getWorld().getClass().getMethod("getHandle").invoke(player.getWorld());
            Object lighobj = constu.newInstance(wh, l.getX(), l.getY(), l.getZ(), false, false);
            Object obj = getNMSClass("PacketPlayOutSpawnEntityWeather").getConstructor(getNMSClass("Entity")).newInstance(lighobj);
            sendPacket(player, obj);
            player.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 100, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"))
                    .invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getNMSVersion() + "." + name);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    private String getNMSVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }


    public String getWeather(int key, int Try) {
        if (Try == 10) {
            return "sun";
        }
        try {
            if (key >= keys.size()) {
                key = 0;
            }
            String weather = "";
            final String search = String.valueOf(this.city + "," + this.country);
            final String url = "http://api.openweathermap.org/data/2.5/weather?q=" + search + "&APPID=" + (instance.getTimeUpdater().isUseKey() ? instance.getTimeUpdater().getKey() : keys.get(key));
            String source = getUrlSource(url);
            if (source == null) {
                return getWeather(key + 1, Try + 1);
            }
            final JSONObject object = toJSON(getUrlSource(url));
            weather = (String) ((JSONObject) ((JSONArray) object.get((Object) "weather")).get(0)).get((Object) "main");
            return weather.toLowerCase();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§4RealTimePlugin: all keys on cooldown!");
            Bukkit.getConsoleSender().sendMessage("§4RealTimePlugin: Report this to rexlNico and setup a own key: https://home.openweathermap.org/users/sign_up");
            return "sun";
        }
    }

    public boolean isTimerActive() {
        return timer;
    }

    public ArrayList<String> getWorlds() {
        return worlds;
    }

    public boolean isActive() {
        return active;
    }

}
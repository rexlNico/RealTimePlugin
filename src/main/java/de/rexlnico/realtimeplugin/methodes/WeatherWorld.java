package de.rexlnico.realtimeplugin.methodes;

import de.rexlnico.realtimeplugin.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class WeatherWorld {

    private int taskID;
    private File file;

    private boolean active;
    private World world;
    private long updateInterval;
    private boolean time;
    private boolean weather;
    private String weatherKey;
    private String timezone;
    private ZoneId zone;
    private String[] weatherLocation;
    private int count, maxCount;

    public WeatherWorld(File file) {
        this.file = file;
        loadElements();
        runUpdate();
        maxCount = new Random(System.nanoTime()).nextInt(3)+1;
    }

    public void runUpdate() {
        if (Bukkit.getScheduler().isCurrentlyRunning(taskID)) Bukkit.getScheduler().cancelTask(taskID);
        if (active && world != null && (weather || time)) {
            if (time) world.setGameRuleValue("doDaylightCycle", "false");
            taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), () -> {
                if (time) world.setTime(getTime());
                if(weather){
                    //world.setStorm(false);
//                    for (Player all : world.getPlayers()) {
                        setWeather(world);
//                    }
                }
            }, 0, 20 * updateInterval);
        }
    }

    public int getTime() {
        Instant nowUtc = Instant.now();
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(nowUtc, zone);
        int time = (int) ((dateTime.getHour() * 60 + dateTime.getMinute()) * (16.6666667f*2f)) - 6000;
        if(time < 0){
            time = 24000 - time;
        }
//        if(time > 24000-6000){
//            time = time = 0;
//        }
        return time;
    }

    public void disable() {
        if (time) world.setGameRuleValue("doDaylightCycle", "true");
        if (Bukkit.getScheduler().isCurrentlyRunning(taskID)) Bukkit.getScheduler().cancelTask(taskID);
    }

    public void update() {
        loadElements();
        runUpdate();
    }

    private void loadElements() {
        if (file.exists()) {
            JSONParser parser = new JSONParser();
            try (Reader reader = new FileReader(file.getPath())) {
                JSONObject object = (JSONObject) parser.parse(reader);

                String worldS = (String) object.get("world");
                World world = Bukkit.getWorld(worldS);
                if (world != null) {
                    this.world = world;
                    this.active = (boolean) object.get("active");
                    this.updateInterval = (long) object.get("updateInterval");
                    this.updateInterval = this.updateInterval < 10 ? 10 : this.updateInterval;
                    JSONObject timeObject = (JSONObject) object.get("time");
                    this.time = (boolean) timeObject.get("active");
                    this.timezone = (String) timeObject.get("timezone");
                    zone = ZoneId.of(timezone);
                    JSONObject weatherObject = (JSONObject) object.get("weather");
                    this.weather = (boolean) weatherObject.get("active");
                    this.weatherKey = (String) weatherObject.get("weatherKey");
                    this.weatherLocation = new String[]{(String) weatherObject.get("City"), (String) weatherObject.get("Country")};
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void setWeather(World world) {
        String weather = getWeather();
        switch (weather) {
            case "rain":
                try {
                    //sendState(player, 2, 0.0f);
                    world.setStorm(true);
                    world.setWeatherDuration(Integer.MAX_VALUE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "thunderstorm":
                try {
                    world.setStorm(true);
                    world.setWeatherDuration(Integer.MAX_VALUE);
                    world.setThundering(true);
                    world.setThunderDuration(Integer.MAX_VALUE);
                    //sendState(player, 2, 0.0f);
//                    if (count == maxCount) {
//                        count = 0;
//                        maxCount = new Random(System.nanoTime()).nextInt(3)+1;
//                    } else {
//                        sendLightning(player, getRandomLocation(player));
//                        count++;
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                try {
//                    sendState(player, 1, 0.0f);
                    world.setStorm(false);
                    world.setWeatherDuration(0);
                    world.setThundering(false);
                    world.setThunderDuration(0);
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


    public String getWeather() {
        try {
            if(weatherKey.isEmpty()){
                return "sun";
            }
            String weather = "";
            final String search = String.valueOf(weatherLocation[0] + "," + weatherLocation[1]);
            final String url = "http://api.openweathermap.org/data/2.5/weather?q=" + search + "&APPID=" + weatherKey;
            String source = getUrlSource(url);
            if (source == null) {
                return "sun";
            }
            final JSONObject object = toJSON(getUrlSource(url));
            weather = (String) ((JSONObject) ((JSONArray) object.get((Object) "weather")).get(0)).get((Object) "main");
            return weather.toLowerCase();
        } catch (Exception e) {
            return "sun";
        }
    }

    public World getWorld() {
        return world;
    }

    public String[] getWeatherLocation() {
        return weatherLocation;
    }

    public String getWeatherKey() {
        return weatherKey;
    }

    public String getTimezone() {
        return timezone;
    }

    public long getUpdateInterval() {
        return updateInterval;
    }

    public File getFile() {
        return file;
    }
}

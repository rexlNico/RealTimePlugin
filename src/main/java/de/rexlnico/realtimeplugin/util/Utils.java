package de.rexlnico.realtimeplugin.util;

import de.rexlnico.realtimeplugin.main.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.logging.Level;

public class Utils {
    private static final Random RAND = new Random();

    public static int overflow(int value, int at) {
        while (value > at) {
            value -= at;
        }
        return value;
    }

    public static byte[] httpRequest(String url) {
        try {
            return httpRequest(new URL(url));
        } catch (MalformedURLException e) {
            //Main.getPlugin().getLogger().log(Level.SEVERE, String.format("HTTP request to %s failed", url), e);
            return null;
        }
    }

    public static byte[] httpRequest(URL url) {
        try {
            URLConnection conn = url.openConnection();
            InputStream in = conn.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            byte[] buff = new byte[1024];
            int len;
            while ((len = in.read(buff)) != -1) {
                bout.write(buff, 0, len);
            }

            bout.close();
            in.close();

            return bout.toByteArray();
        } catch (Exception e) {
            //Main.getPlugin().getLogger().log(Level.SEVERE, String.format("HTTP request to %s failed", url), e);
            return null;
        }
    }

    public static Location getRandomLocation(Player player) {
        Location location;
        int x;
        int z;
        x = RAND.nextInt(player.getLocation().getBlockX() + 25) + 12;
        z = RAND.nextInt(player.getLocation().getBlockZ() + 25) + 12;
        if (RAND.nextBoolean()) {
            x *= -1;
        }
        if (RAND.nextBoolean()) {
            z *= -1;
        }
        location = new Location(player.getWorld(), player.getLocation().add(x, 0, z).getX(), player.getWorld().getHighestBlockYAt(x, z) + 1, player.getLocation().add(x, 0, z).getZ());
        return location;
    }

    public static JSONObject parseJSON(String json) {
        return (JSONObject) JSONValue.parse(json);
    }

}

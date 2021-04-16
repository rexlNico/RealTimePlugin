package de.rexlnico.realtimeplugin.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public class NMSUtil {
    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName(String.format("net.minecraft.server.%s.%s", VERSION, name));
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"))
                    .invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendLightning(Player player, Location l) {
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

    public static void sendGameState(Player player, int type, float state) throws InstantiationException, NoSuchFieldException {
        try {
            final Object entityPlayer = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
            final Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
            final Object packet = getNMSClass("PacketPlayOutGameStateChange").getConstructor(Integer.TYPE, Float.TYPE).newInstance(type, state);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception ignored) {
        }
    }
}

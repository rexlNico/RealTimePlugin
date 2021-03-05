package de.rexlnico.realtimeplugin.methodes;

import de.rexlnico.realtimeplugin.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Time;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.TimeZone;

public class WorldManager {

    ArrayList<WeatherWorld> worlds;
    private ArrayList<String> tabComplete;

    public WorldManager() throws IOException {
        worlds = new ArrayList<WeatherWorld>();
        tabComplete = new ArrayList<>();
        loadAll();
        setTabComplete();
        createTimeTable();
    }

    private void loadAll() throws IOException {
        File directoryPath = new File("plugins/RealTimePlugin/worlds");
        File fileList[] = directoryPath.listFiles();
        if (fileList == null) {
            createExample(directoryPath);
            return;
        }
        for (File file : fileList) {
            if (file.getName().contains(".json")) {
                worlds.add(new WeatherWorld(file));
            }
        }
    }

    public void loadNew() {
        File directoryPath = new File("plugins/RealTimePlugin/worlds");
        File fileList[] = directoryPath.listFiles();
        if (fileList == null) return;
        for (File file : fileList) {
            if (file.getName().contains(".json")) {
                if(containsFile(file.getName())){
                    worlds.add(new WeatherWorld(file));
                }
            }
        }
        setTabComplete();
    }

    private void createTimeTable() throws IOException {
        File file = new File("plugins/RealTimePlugin/WeatherTimezones.cfg");
        if(file.exists()) return;
        YamlConfiguration.loadConfiguration(file).save(file);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        for(String s : ZoneId.getAvailableZoneIds()){
            writer.write(s+"\n");
        }
        writer.flush();
        writer.close();
    }

    public ArrayList<String> getTabComplete(){
        return tabComplete;
    }

    private void setTabComplete(){
        tabComplete.clear();
        for (WeatherWorld world : worlds) {
            tabComplete.add(world.getFile().getName());
        }
    }

    public WeatherWorld getWeatherWorld(String name){
        for (WeatherWorld world : worlds) {
            if (world.getFile().getName().equals(name)) {
                return world;
            }
        }
        return null;
    }

    private boolean containsFile(String name){
        for (WeatherWorld world : worlds) {
            if (world.getFile().getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void updateAll() {
        for (WeatherWorld weatherWorld : worlds) {
            weatherWorld.update();
        }
        setTabComplete();
    }

    public void disable() {
        for (WeatherWorld weatherWorld : worlds) {
            weatherWorld.disable();
        }
    }

    private void createExample(File dir) throws IOException {
        InputStream root = Main.getPlugin().getResource("example.json");
        File filePath = new File("plugins/RealTimePlugin/worlds", "example.json");
        YamlConfiguration.loadConfiguration(filePath).save(filePath);
        Files.copy(root, filePath.toPath(), StandardCopyOption.REPLACE_EXISTING);


//        File filePath = new File("plugins/RealTimePlugin/worlds", "example.json");
//        YamlConfiguration.loadConfiguration(filePath).save(filePath);
//        org.json.simple.JSONObject object = new org.json.simple.JSONObject();
//
//        object.put("world", "worldName");
//        object.put("updateInterval", 20L);
//        object.put("active", false);
//        JSONObject time = new JSONObject();
//        time.put("active", true);
//        time.put("timezone", "Europe/Berlin");
//        object.put("time", time);
//        JSONObject weather = new JSONObject();
//        weather.put("active", true);
//        weather.put("City", "Berlin");
//        weather.put("Country", "Germany");
//        weather.put("weatherKey", "");
//        object.put("weather", weather);
//
//        try (FileWriter file = new FileWriter(filePath.getPath())) {
//
//            file.write(object.toJSONString());
//            file.flush();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
